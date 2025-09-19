# Zowe's default SSH requests handling implementation

## [DatasetsAPI](datasets/SshDatasetsAPI.kt) functions

| Method                     | Implemented | Test coverage |
|----------------------------|-------------|---------------|
| listDatasets               | ✅           | ✅             |
| getDatasetInfo             | ✅           | ✅             |
| listDatasetMembers         | ✅           | ✅             |
| retrieveDatasetContent     | ✅           | ✅             |
| writeToDataset             | ❌           | ❌             |
| createDataset              | ✅           | ✅             |
| deleteDataset              | ✅           | ✅             |
| renameDataset              | ❌           | ❌             |
| copyDataset                | ❌           | ❌             |
| migrateDataset             | ❌           | ❌             |
| recallDataset              | ❌           | ❌             |
| deleteDatasetBackupVersion | ❌           | ❌             |
