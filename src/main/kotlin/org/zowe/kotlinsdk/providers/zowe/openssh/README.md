# Zowe's default SSH requests handling implementation

## [DatasetsAPI](datasets/SshDatasetsAPI.kt) functions

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
| copyDataset                | ❌           | ❌             |
| migrateDataset             | ❌           | ❌             |
| recallDataset              | ❌           | ❌             |
| deleteDatasetBackupVersion | ❌           | ❌             |

## [FilesAPI](files/SshFilesAPI.kt) functions

| Method    | Implemented | Test coverage |
|-----------|-------------|---------------|
| listFiles | ❌           | ❌             |

## [JesAPI](jes/SshJesAPI.kt) functions

| Method   | Implemented | Test coverage |
|----------|-------------|---------------|
| getJob   | ✅           | ✅             |
| listJobs | ❌           | ❌             |
