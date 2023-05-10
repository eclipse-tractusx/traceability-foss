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
In order to upload data to EDC Provider, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/local/testing/testdata/transform-and-upload.py)
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

- Now you should be able to send notifications for that assetId from BPN:BPNL00000003B2OM to BPN:BPNL00000003AYRE


## Structure of a Testdata
Consists of a List of the following entries:
```json
{
    "catenaXId" : "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22",
    "bpnl" : "BPNL00000003CML1",
    "urn:bamm:io.catenax.assembly_part_relationship:1.1.1#AssemblyPartRelationship" : [ {
      "catenaXId" : "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22",
      "childParts" : [ {
        "quantity" : {
          "quantityNumber" : 1,
          "measurementUnit" : {
            "datatypeURI" : "urn:bamm:io.openmanufacturing:meta-model:1.0.0#curie",
            "lexicalValue" : "unit:piece"
          }
        },
        "lifecycleContext" : "AsBuilt",
        "assembledOn" : "2022-02-03T14:48:54.709Z",
        "lastModifiedOn" : "2022-02-03T14:48:54.709Z",
        "childCatenaXId" : "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd"
      } ]
    } ],
    "urn:bamm:io.catenax.serial_part_typization:1.1.0#SerialPartTypization" : [ {
      "localIdentifiers" : [ {
        "value" : "BPNL00000003CML1",
        "key" : "manufacturerId"
      }, {
        "value" : "IF-53",
        "key" : "manufacturerPartId"
      }, {
        "value" : "OMAOYGBDTSRCMYSCX",
        "key" : "partInstanceId"
      }, {
        "value" : "OMAOYGBDTSRCMYSCX",
        "key" : "van"
      } ],
      "manufacturingInformation" : {
        "date" : "2018-09-28T04:15:57.000Z",
        "country" : "DEU"
      },
      "catenaXId" : "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22",
      "partTypeInformation" : {
        "manufacturerPartId" : "IF-53",
        "classification" : "product",
        "nameAtManufacturer" : "Vehicle Hybrid"
      }
    } ]
  }
```

## Building downward Relationships in Testdata
Is achieved by defining the order of the BPNLs of the desired manufacturers. See example below:

```json
  [
   {
    "catenaXId" : "urn:uuid:7c7d5aec-b15d-491c-8fbd-c61c6c02c69a",
    "bpnl" : "BPNL00000003AZQP"
    },
   {
    "catenaXId" : "urn:uuid:7c7d5aec-b15d-491c-8fbd-c61c6c02c69a",
    "bpnl" : "BPNL00000003ML1"

    },
   {
    "catenaXId" : "urn:uuid:7c7d5aec-b15d-491c-8fbd-c61c6c02c69a",
    "bpnl" : "BPNL00000003CNKC"

    }
  ]
```
...AZQP -> ...3ML1 -> ...CNKC
and adding an AssemlyPartRelationship Aspect with the corresponding childCatenaXId:
```json
{
"urn:bamm:io.catenax.assembly_part_relationship:1.1.0#AssemblyPartRelationship" : [ {
"catenaXId" : "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd",
"childParts" : [ {
"quantity" : {
"quantityNumber" : 1,
"measurementUnit" : {
"datatypeURI" : "urn:bamm:io.openmanufacturing:meta-model:1.0.0#curie",
"lexicalValue" : "unit:piece"
    }
  },
"lifecycleContext" : "AsBuilt",
"assembledOn" : "2022-02-03T14:48:54.709Z",
"lastModifiedOn" : "2022-02-03T14:48:54.709Z",
"childCatenaXId" : "urn:uuid:1d2d8480-90a5-4a17-9ecb-2ff039d35fec"
    } ]
  } ]
}
```


## Building upward Relationships in Testdata

Is achieved through adding the SingleLevelusageBuilt - Codeblock and the corresponding parent catenaXId above, see example below (Only SingleLevelUsageBuilt expanded):

```json
[{
  "catenaXId" : "urn:uuid:f11ddc62-3bd5-468f-b7b0-110fe13ed0cd",
  "bpnl" : "BPNL00000003CNKC",
  "urn:bamm:io.catenax.assembly_part_relationship:1.1.1#AssemblyPartRelationship": "[...]",
  "urn:bamm:io.catenax.serial_part_typization:1.1.0#SerialPartTypization" : "[...]"
  }, {
  "catenaXId" : "urn:uuid:4e390dab-707f-446e-bfbe-653f6f5b1f37",
  "bpnl" : "BPNL00000003AZQP",
  "urn:bamm:io.catenax.serial_part_typization:1.1.0#SerialPartTypization" : "",
  "urn:bamm:io.catenax.single_level_usage_as_built:1.0.1#SingleLevelUsageAsBuilt": [
    {
      "parentParts": [
        {
          "parentCatenaXId": "urn:uuid:5205f736-8fc2-4585-b869-6bf36842369a",
          "quantity": {
            "quantityNumber": 1,
            "measurementUnit": "unit:piece"
          },
          "createdOn": "2023-02-03T14:48:54.709Z",
          "lastModifiedOn": "2023-02-03T14:48:54.709Z"
        }
      ],
      "catenaXId": "urn:uuid:4e390dab-707f-446e-bfbe-653f6f5b1f37"
    }
  ]
}
]
```
...AZQP -> CNKC -> ...

