# Zowe's default z/OSMF REST API requests handling implementation

## [DatasetsAPI](datasets/ZosmfDatasetsAPI.kt) functions

| Method                     | Implemented | Test coverage |
|----------------------------|-------------|---------------|
| listDatasets               | ✅           | ✅             |
| getDatasetInfo             | ✅           | ✅             |
| listDatasetMembers         | ✅           | ✅             |
| retrieveDatasetContent     | ✅           | ✅             |
| writeToDataset             | ✅           | ✅             |
| createDataset              | ✅           | ✅             |
| deleteDataset              | ✅           | ✅             |
| renameDataset              | ✅           | ✅             |
| copyDataset                | ✅           | ❌             |
| migrateDataset             | ✅           | ❌             |
| recallDataset              | ✅           | ❌             |
| deleteDatasetBackupVersion | ✅           | ❌             |

## [FilesAPI](files/ZosmfFilesAPI.kt) functions

| Method                   | Implemented | Test coverage |
|--------------------------|-------------|---------------|
| listFiles                | ✅           | ✅             |
| retrieveFileContent      | ✅           | ❌             |
| writeToUssFile           | ✅           | ❌             |
| createFile               | ✅           | ❌             |
| deleteFile               | ✅           | ❌             |
| copyFile                 | ✅           | ❌             |
| moveFile                 | ✅           | ❌             |
| changeFileMode           | ✅           | ❌             |
| changeFileOwner          | ✅           | ❌             |
| changeFileTag            | ✅           | ❌             |
| fileExtAttributesUtility | ✅           | ❌             |
| getFileACL               | ✅           | ❌             |
| setFileACL               | ✅           | ❌             |
| linkFile                 | ✅           | ❌             |
| unlinkFile               | ✅           | ❌             |

## [JesAPI](jes/ZosmfJesAPI.kt) functions

| Method | Implemented | Test coverage |
|--------|-------------|---------------|
| getJob | ✅           | ✅             |

## [InfoAPI](info/ZosmfInfoAPI.kt) functions

| Method        | Implemented | Test coverage |
|---------------|-------------|---------------|
| getSystemInfo | ✅           | ❌             |
