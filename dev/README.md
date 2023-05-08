# Full reset of environment (dev,test)
## 1) Clean up

- Open Argo specific env
- Delete all pvc (not pgadmin, not minio)
- Delete related database pods
- Delete related deployments
- Wait until pvc and database pods are restored
- Sync full application with prune and replace

Repeat those steps for registry, submodelserver, trace-x-provider-edcs, tracex-instances

## 2) Data upload of assets
In order to upload data to EDC Provider, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/testdata-transform/transform-and-upload.py)
Sample invocation:

```
python transform-and-upload.py -f CX_Testdata_v1.4.1-AsBuilt-reduced-with-asPlanned.json -s https://tracex-submodel-server.dev.demo.catena-x.net https://tracex-submodel-server.dev.demo.catena-x.net -edc https://trace-x-test-edc.dev.demo.catena-x.net https://trace-x-edc.dev.demo.catena-x.net -a https://trace-x-registry.dev.demo.catena-x.net/semantics/registry -k apiKey
```

where:

* -f file to be used for data provisioning
* -s submodel server url(s)
* -edc edc url(s) to upload data to
* -a aas url(s)
* -k edc api key (value from <path:traceability-foss/data/dev/edc/controlplane#edc.api.control.auth.apikey.value> vault path)

## 3) Prepare trace-x
- Registry reload
```
curl --request GET --url <baseUrl>/api/registry/reload
```
- Create notification contracts
```
curl --request POST \
   --url <baseUrl>/api/edc/notification/contract \
   --header 'Authorization: Bearer x' \
   --header 'Content-Type: application/json' \
   --data '{
   "notificationType": "QUALITY_INVESTIGATION", "notificationMethod": "RECEIVE"
   }
```
```
curl --request POST \
--url https://traceability.dev.demo.catena-x.net/api/edc/notification/contract \
--header 'Authorization: Bearer x' \
--header 'Content-Type: application/json' \
--data '{"notificationType" : "QUALITY_INVESTIGATION", "notificationMethod" : "UPDATE"}'
-
```
## 4) Manual adaption for notification flow
- Open pgadmin on test
- select asset which relates to dev
```
UPDATE asset
SET owner = 0 where id = 'urn:uuid:51ff7c73-34e9-45d4-816c-d92ownerbpna';

UPDATE asset
SET owner = 2 where id = 'urn:uuid:51ff7c73-34e9-45d4-816c-d92ownerbpnb';;
```
