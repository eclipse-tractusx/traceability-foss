# Edc provider data upload

## Data upload of assets

In order to upload data to EDC Provider, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/testdata-transform/transform-and-upload.py)
Sample invocation:

```
python3 transform-and-upload.py -f CX_Testdata_v1.4.1-AsBuilt-reduced-with-asPlanned.json -s https://tracex-submodel-server.dev.demo.catena-x.net -edc https://trace-x-edc.dev.demo.catena-x.net https://trace-x-test-edc.dev.demo.catena-x.net https://tracex-consumer-controlplane.dev.demo.catena-x.net https://tracex-test-consumer-controlplane.dev.demo.catena-x.net -a https://trace-x-registry.dev.demo.catena-x.net -k edcApiKey
```

where:

* -f file to be used for data provisioning
* -s submodel server url(s)
* -edc edc url(s) to upload data to
* -a aas url(s)
* -k edc api key (value from <path:traceability-foss/data/dev/edc/controlplane#edc.api.control.auth.apikey.value> vault path)

# Cleaning up assets

In order to clean up EDC Provider data, please use [IRS project script](https://github.com/catenax-ng/tx-item-relationship-service/blob/main/testdata-transform/reset-env.py)

Sample Invocation:

```
python3 reset-env.py -a "https://trace-x-registry.dev.demo.catena-x.net" -edc "https://trace-x-edc.dev.demo.catena-x.net"  -k "EDC API Key"

```

where:

* -edc edc url(s) to upload data to
* -a aas url(s)
* -k edc api key (value from <path:traceability-foss/data/dev/edc/controlplane#edc.api.control.auth.apikey.value> vault path)
