/*
 * Copyright 2025 ScreamingSandals
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.screamingsandals.reposilite.webhooks

import com.reposilite.configuration.shared.SharedConfigurationFacade
import com.reposilite.maven.MavenFacade
import com.reposilite.maven.api.DeployEvent
import com.reposilite.plugin.api.Facade
import com.reposilite.plugin.api.Plugin
import com.reposilite.plugin.api.ReposiliteDisposeEvent
import com.reposilite.plugin.api.ReposilitePlugin
import com.reposilite.plugin.event
import com.reposilite.plugin.facade
import org.eclipse.jetty.xml.XmlParser
import org.screamingsandals.reposilite.webhooks.discord.sendDiscordWebhook

@Plugin(
    name = "screamingsandals-webhooks-plugin",
    dependencies = ["configuration", "local-configuration", "shared-configuration"],
    settings = WebhookSettings::class
)
class WebhookPlugin : ReposilitePlugin() {
    override fun initialize(): Facade? {
        val domainSettings = facade<SharedConfigurationFacade>().getDomainSettings<WebhookSettings>()
        val maven = facade<MavenFacade>()

        event { event: DeployEvent ->
            if (!event.gav.toString().endsWith(".pom")) {
                return@event
            }

            val username = event.by.split('@', limit = 2)[0]
            val repositoryName = event.repository.name

            var gav = event.gav.getParent().toString()
            val split = gav.reversed().split('/', limit = 3)
            if (split.size != 3) {
                return@event
            }

            val version = split[0].reversed()
            val artifactId = split[1].reversed()
            val groupId = split[2].reversed().replace('/', '.')
            val simpleName = event.gav.getSimpleName().replace(".pom", "")

            val deployment by lazy {
                DeploymentInformation(
                    username = username,
                    repositoryName = repositoryName,
                    groupId = groupId,
                    artifactId = artifactId,
                    version = version,
                    simpleName = simpleName,
                    folderLocation = event.gav.getParent().toString(),
                    pomProperties = event.repository.storageProvider.getFile(event.gav).let { file ->
                        if (!file.isOk) {
                            return@let emptyMap()
                        }

                        buildMap {
                            XmlParser()
                                .parse(file.get())
                                .get("properties")
                                ?.forEach { child ->
                                    if (child is XmlParser.Node) {
                                        put(child.tag, child.toString(false, true))
                                    }
                                }
                        }
                    }
                )
            }

            domainSettings.get().discordWebhooks.forEach { webhook ->
                if (
                    webhook.artifactFilter.any { filter ->
                        (filter.repositoryName.isEmpty() || filter.repositoryName == repositoryName)
                                && (filter.groupRegex.isEmpty() || groupId.matches(Regex(filter.groupRegex)))
                                && (filter.idRegex.isEmpty() || artifactId.matches(Regex(filter.idRegex)))
                                && (filter.versionRegex.isEmpty() || version.matches(Regex(filter.versionRegex)))
                    }
                ) {
                        sendDiscordWebhook(webhook, deployment)
                }
            }
        }

        event { _: ReposiliteDisposeEvent ->

        }

        return null
    }
}