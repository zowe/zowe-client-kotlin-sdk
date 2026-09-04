# Zowe Client Kotlin SDK global API specification

## [DatasetsAPI](datasets/api/DatasetsAPI.kt) functions

- listDatasets
- getDatasetInfo
- listDatasetMembers
- retrieveDatasetContent
- writeToDataset
- createDataset
- deleteDataset
- renameDataset
- copyDataset
- migrateDataset
- recallDataset
- deleteDatasetBackupVersion

## [FilesAPI](files/api/FilesAPI.kt) functions

- listFiles
- retrieveFileContent
- writeToUssFile
- createFile
- deleteFile
- copyFile
- moveFile
- changeFileMode
- changeFileOwner
- changeFileTag
- fileExtAttributesUtility
- getFileACL
- setFileACL
- linkFile
- unlinkFile

## [JesAPI](jes/api/JesAPI.kt) functions

- getJob

## [InfoAPI](info/api/InfoAPI.kt) functions

- getSystemInfo

## [TsoAPI](tso/api/TsoAPI.kt) functions

- startTso
- endTso
- startApplication
- issueCommand
- getCommandResult
- sendToTso
- receiveFromTso
- sendToApplication
- receiveFromApplication
- pingTso
