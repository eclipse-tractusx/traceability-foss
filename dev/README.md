# Full reset of environment (dev,test)

## 1) Clean up

### a) Using Insomnia Collection

- Hint you need to copy the access token for argo cd into the specific environment for requests against argo (argocd.token) in insomnia: argo_access_token

Here you can find the [Trace-X Insomnia Collection](https://github.com/eclipse-tractusx/traceability-foss/blob/main/tx-backend/collection/tracex.json). Use this [README](https://github.com/eclipse-tractusx/traceability-foss/blob/main/tx-backend/collection/README.md) to find out how to setup Insomnia with the Trace-X Collection.

In the Collection you will find a directory named 'Argo', in which you can delete & sync all necessary application components. Go through every directory inside the 'Argo' directory and execute every request inside the Directory 'DELETE'. To make this step easier, you can install the Insomnia Plugin ['multiple requests'](https://insomnia.rest/plugins/insomnia-plugin-multiple-requests). With this Plugin you can execute all requests inside the 'DELETE' Directory by right-clicking the directory and choosing 'send Requests'.

Wait until pvc and database pods are restored. Underneath all 'DELETE' directories there is another request that syncs the application component. Execute this request for all. The environment begins the sync process. After the sync process you can proceed with Chapter 2.

### b) Manual steps

- Open Argo specific env
- Delete all pvc (not pgadmin, not minio)
- Delete related database pods
- Delete related deployments
- Wait until pvc and database pods are restored
- Sync full application with prune and replace

Repeat those steps for registry, trace-x-provider-edcs, tracex-instances

## 2) Data upload of assets

In order to upload data to EDC Provider, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/local/testing/testdata/transform-and-upload.py)

Keep in mind to always update DEV and TEST since they are dependend on each other.

Sample invocation (DEV)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability.dev.demo.catena-x.net/api/submodel -edc https://trace-x-edc.dev.demo.catena-x.net -a https://trace-x-registry.dev.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-dataplane.dev.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC
```

Sample invocation (TEST)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-test.dev.demo.catena-x.net/api/submodel -edc https://trace-x-test-edc.dev.demo.catena-x.net -a https://trace-x-registry-test.dev.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-test-edc-dataplane.dev.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC
```

Sample invocation (E2E A)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-e2e-a.dev.demo.catena-x.net/api/submodel -edc https://trace-x-edc-e2e-a.dev.demo.catena-x.net -a https://trace-x-registry-e2e-a.dev.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-e2e-a-dataplane.dev.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC
```

Sample invocation (E2E B)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-e2e-b.dev.demo.catena-x.net/api/submodel -edc https://trace-x-edc-e2e-b.dev.demo.catena-x.net -a https://trace-x-registry-e2e-b.dev.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-e2e-b-dataplane.dev.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC
```

Sample invocation (INT A)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-int-a.int.demo.catena-x.net/api/submodel -edc https://trace-x-edc-int-a.int.demo.catena-x.net -a https://trace-x-registry-int-a.int.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-int-a-dataplane.int.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV
```

Sample invocation (INT B)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-int-b.int.demo.catena-x.net/api/submodel -edc https://trace-x-edc-int-b.int.demo.catena-x.net -a https://trace-x-registry-int-b.int.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-int-b-dataplane.int.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV  --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV
```

Sample invocation (STABLE A)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-stable-a.stable.demo.catena-x.net/api/submodel -edc https://trace-x-edc-stable-a.stable.demo.catena-x.net -a https://trace-x-registry-stable-a.stable.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-stable-a-dataplane.stable.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV  --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV
```

Sample invocation (STABLE B)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-stable-b.stable.demo.catena-x.net/api/submodel -edc https://trace-x-edc-stable-b.stable.demo.catena-x.net -a https://trace-x-registry-stable-b.stable.demo.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-stable-b-dataplane.stable.demo.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV  --allowedBPNs BPNL00000003CML1 BPNL00000003CNKC BPNL00000003AZQP BPNL00000003CSGV
```

Sample invocation (ASSOCIATION INT A)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-int-a.int.catena-x.net/api/submodel -edc https://trace-x-edc-int-a.int.catena-x.net -a https://trace-x-registry-int-a.int.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-int-a-dataplane.int.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL000000000UKM BPNL000000000DWF BPNL000000000EVQ BPNL00000003CSGV  --allowedBPNs BPNL000000000UKM BPNL000000000DWF BPNL00000003AZQP BPNL00000003CSGV
```

Sample invocation (ASSOCIATION INT B)

```
python transform-and-upload.py -f CX_Testdata_MessagingTest_v0.0.12.json -s https://traceability-int-b.int.catena-x.net/api/submodel -edc https://trace-x-edc-int-b.int.catena-x.net -a https://trace-x-registry-int-b.int.catena-x.net/semantics/registry/api/v3.0 -d https://trace-x-edc-int-b-dataplane.int.catena-x.net -p id-3.0-trace -k <apiKey> --aas3 --edcBPN BPNL000000000UKM BPNL000000000DWF BPNL000000000EVQ BPNL00000003CSGV  --allowedBPNs BPNL000000000UKM BPNL000000000DWF BPNL00000003AZQP BPNL00000003CSGV
```

where:

* -f file to be used for data provisioning /tx-backend/testdata/CX_Testdata_MessagingTest_vx.x.x.json
* -s submodel server url(s) -- ( currently pointing to our backend api as it was implemented under /api/submodel )
* -edc edc url(s) to upload data to
* -a aas url(s)
* -p policies to add to the data
* -k edc api key (value from <path:traceability-foss/data/dev/edc/controlplane#edc.api.control.auth.apikey.value> vault path)

optional:

* -bpns upload data only from a list of BPN

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

```
curl --request POST \
   --url <baseUrl>/api/edc/notification/contract \
   --header 'Authorization: Bearer x' \
   --header 'Content-Type: application/json' \
   --data '{
   "notificationType": "QUALITY_ALERT", "notificationMethod": "RECEIVE"
   }
```

```
curl --request POST \
--url https://traceability.dev.demo.catena-x.net/api/edc/notification/contract \
--header 'Authorization: Bearer x' \
--header 'Content-Type: application/json' \
--data '{"notificationType" : "QUALITY_ALERT", "notificationMethod" : "UPDATE"}'
-
```

## Structure of Testdata

Consists of a List of the following structured entries:

```json
{
    "catenaXId": "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22",
    "bpnl": "BPNL00000003CML1",
    "urn:samm:io.catenax.single_level_usage_as_built:3.0.0#SingleLevelUsageAsBuilt" : [
        {
            "catenaXId" : "urn:uuid:7eeeac86-7b69-444d-81e6-655d0f1513bd",
            "customers" : [
                "BPNL00000003CML1"
            ],
            "parentItems" : [
                {
                    "quantity" : {
                        "value" : 1,
                        "unit" : "unit:piece"
                    },
                    "catenaXId" : "urn:uuid:6b2296cc-26c0-4f38-8a22-092338c36e22",
                    "createdOn" : "2023-05-29T14:48:54.709Z",
                    "lastModifiedOn" : "2023-02-03T14:48:54.709Z",
                    "businessPartner" : "BPNL00000003CML1",
                    "isOnlyPotentialParent" : false
                }
            ]
        }
    ],
    "urn:bamm:io.catenax.serial_part:1.0.0#SerialPart" : [ {
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
       "bpnl": "BPNL00000003CNKC"

    }
  ]
```
...AZQP -> ...3ML1 -> ...CNKC
and adding an SingleLevelBomAsBuilt Aspect with the corresponding childCatenaXId:
```json
{
"urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt" : [ {
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

...AZQP -> CNKC -> ...

