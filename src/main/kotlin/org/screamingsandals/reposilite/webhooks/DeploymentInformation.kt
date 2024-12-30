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

data class DeploymentInformation(
    val username: String,
    val repositoryName: String,
    val groupId: String,
    val artifactId: String,
    val version: String,
    val simpleName: String,
    val folderLocation: String,
    val pomProperties: Map<String, String>,
) {
    private val computedPlaceholders by lazy {
        mapOf(
            "username" to username,
            "repositoryName" to repositoryName,
            "groupId" to groupId,
            "artifactId" to artifactId,
            "version" to version,
            "simpleName" to simpleName,
            "folderLocation" to folderLocation,
        ) + pomProperties.mapKeys { (key, _) -> "properties.$key" }
    }

    fun resolvePlaceholders(input: String): String {
        return Regex("\\$\\{([^}]+)}").replace(input) { matchResult ->
            computedPlaceholders[matchResult.groupValues[1]] ?: matchResult.value
        }
    }
}