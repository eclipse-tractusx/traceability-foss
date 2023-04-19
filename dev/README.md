# Full reset of environment (dev,test)
##1) Clean up

- Open Argo specific env
- Delete all pvc (not pgadmin, not minio)
- Delete related database pods
- Delete related deployments
- Wait until pvc and database pods are restored
- Sync full application with prune and replace

Repeat those steps for registry, submodelserver, trace-x-provider-edcs, tracex-instances

##2) Data upload of assets
In order to upload data to EDC Provider, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/testdata-transform/transform-and-upload.py)
Sample invocation:

```
py transform-and-upload.py -f CX_Testdata_v1.4.1-AsBuilt-reduced-with-asPlanned.json -s https://tracex-submodel-server.dev.demo.catena-x.net https://tracex-submodel-server.dev.demo.catena-x.net -edc https://trace-x-test-edc.dev.demo.catena-x.net https://trace-x-edc.dev.demo.catena-x.net -a https://trace-x-registry.dev.demo.catena-x.net -k apiKey
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
### BPN A -> DEV(BPNL00000003AYRE) and BPN B -> TEST(BPNL00000003B2OM)
- Open pgadmin on dev
- select asset which relates to test
```
SELECT id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id, manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type, supplier_part, batch_id, part_instance_id, van
FROM public.asset where manufacturer_id = 'BPNL00000003B2OM';
```
Output of select:
```
"id"	"customer_part_id"	"id_short"	"manufacturer_id"	"manufacturer_name"	"manufacturer_part_id"	"manufacturing_country"	"manufacturing_date"	"name_at_customer"	"name_at_manufacturer"	"quality_type"	"supplier_part"	"batch_id"	"part_instance_id"	"van"
"urn:uuid:51ff7c73-34e9-45d4-816c-d92578843e68"	"1O222E8-43"	"--"	"BPNL00000003B2OM"	"Tier A"	"1O222E8-43"	"DEU"	"2022-02-04 13:48:54"	"Transmission"	"Transmission"	0	true	"--"	"NO-712627233731926672258402"	"--"
```
- Open pagmin on test
- take the select result and insert with small adaptions into test database (add manufacturer_id = BPNL00000003AYRE, supplierPart = false)
- hint: if it already exists, just adapt it to have the two values mentioned before updated
```
INSERT INTO asset (id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id, manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type, supplier_part, batch_id, part_instance_id, van)
VALUES ('urn:uuid:51ff7c73-34e9-45d4-816c-d92578843e68', '1O222E8-43', '--', 'BPNL00000003AYRE', 'Tier A', '1O222E8-43', 'DEU', '2022-02-04 13:48:54', 'Transmission', 'Transmission', 0, false, '--', 'NO-712627233731926672258402', NULL);

```
- Now you should be able to send notifications for that assetId from BPN: BPNL00000003AYRE to BPN: BPNL00000003B2OM
### BPN B -> TEST(BPNL00000003B2OM) and BPN A -> DEV(BPNL00000003AYRE)
#### Hint - The following asset MUST NOT be the same as above configured
- Open pgadmin on test
- select asset which relates to dev
```
SELECT id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id, manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type, supplier_part, batch_id, part_instance_id, van
FROM public.asset;
```
- Take one which is setup as supplier part and update manufacturer_id to be: BPNL00000003AYRE
Example:
```
"id"	"customer_part_id"	"id_short"	"manufacturer_id"	"manufacturer_name"	"manufacturer_part_id"	"manufacturing_country"	"manufacturing_date"	"name_at_customer"	"name_at_manufacturer"	"quality_type"	"supplier_part"	"batch_id"	"part_instance_id"	"van"
"urn:uuid:4b2b21d0-8fed-4d32-b262-f75c5b846df8"	"35360R3-90"	"--"	"BPNL00000003AYRE"	"N-Tier A"	"35360R3-90"	"DEU"	"2022-02-04 13:48:54"	"Engineering Plastics"	"Engineering Plastics"	0	true	"--"	"NO-989414344642730064939021"	"--"
```
- Open pagmin on test
- take the result and insert with small adaptions into test database (add manufacturer_id = BPNL00000003B2OM, supplierPart = false)
- hint: if it already exists, just adapt it to have the two values mentioned before updated
```
INSERT INTO asset (id, customer_part_id, id_short, manufacturer_id, manufacturer_name, manufacturer_part_id, manufacturing_country, manufacturing_date, name_at_customer, name_at_manufacturer, quality_type, supplier_part, batch_id, part_instance_id, van)
VALUES ('urn:uuid:4b2b21d0-8fed-4d32-b262-f75c5b846df8', '35360R3-90', '--', 'BPNL00000003B2OM', 'N-Tier A', '35360R3-90', 'DEU', '2022-02-04 13:48:54', 'Engineering Plastics', 'Engineering Plastics', 0, false, '--', 'NO-989414344642730064939021', NULL);

```
- Now you should be able to send notifications for that assetId from BPN:BPNL00000003B2OM to BPN:BPNL00000003AYRE

