# Zowe's default SSH requests handling implementation

## [DatasetsAPI](datasets/SshDatasetsAPI.kt) functions

| Method                     | Implemented | Test coverage |
|----------------------------|-------------|---------------|
| listDatasets               | ✅          | ✅            |
| getDatasetInfo             | ✅          | ✅            |
| listDatasetMembers         | ✅          | ✅            |
| retrieveDatasetContent     | ✅          | ✅            |
| writeToDataset             | ✅          | ✅            |
| createDataset              | ✅          | ✅            |
| deleteDataset              | ✅          | ✅            |
| renameDataset              | ✅          | ✅            |
| copyToDataset              | ✅          | ✅            |
| migrateDataset             | ❌          | ❌            |
| recallDataset              | ❌          | ❌            |
| deleteDatasetBackupVersion | ❌          | ❌            |

## [FilesAPI](files/SshFilesAPI.kt) functions

| Method                   | Implemented | Test coverage |
|--------------------------|-------------|---------------|
| listFiles                | ✅          | ✅            |
| retrieveFileContent      | ✅          | ✅            |
| writeToFile              | ✅          | ✅            |
| createFile               | ✅          | ✅            |
| deleteFile               | ✅          | ✅            |
| copyFile                 | ✅          | ✅            |
| moveFile                 | ✅          | ✅            |
| changeFileMode           | ✅          | ✅            |
| changeFileOwner          | ✅          | ✅            |
| changeFileTag            | ✅          | ✅            |
| fileExtAttributesUtility | ✅          | ✅            |
| getFileACL               | ✅          | ✅            |
| setFileACL               | ✅          | ✅            |
| linkFile                 | ✅          | ✅            |
| unlinkFile               | ✅          | ✅            |

## [JesAPI](jes/SshJesAPI.kt) functions

| Method   | Implemented | Test coverage |
|----------|-------------|---------------|
| getJob   | ✅          | ✅            |
| listJobs | ✅          | ✅            |
