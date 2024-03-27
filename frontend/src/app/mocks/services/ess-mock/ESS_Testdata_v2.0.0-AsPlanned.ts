export const mock_ESS_Testdata_v2_0_0_AsPlanned = [
{
  "policies": {
    "ID 3.0 Trace": {
      "@context": {
        "odrl": "http://www.w3.org/ns/odrl/2/"
      },
      "@type": "PolicyDefinitionRequestDto",
      "@id": "id-3.0-trace",
      "policy": {
        "@type": "Policy",
        "odrl:permission": [
          {
            "odrl:action": "USE",
            "odrl:constraint": {
              "@type": "AtomicConstraint",
              "odrl:or": [
                {
                  "@type": "Constraint",
                  "odrl:leftOperand": "PURPOSE",
                  "odrl:operator": {
                    "@id": "odrl:eq"
                  },
                  "odrl:rightOperand": "ID 3.0 Trace"
                }
              ]
            }
          }
        ]
      }
    }
  },
  "https://catenax.io/schema/TestDataContainer/1.0.0": [
    {
      "catenaXId": "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e",
      "bpnl": "BPNL00000003AYRE",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2019-04-04T03:19:03.000Z",
            "validTo": "2024-12-29T10:25:12.000Z"
          },
          "catenaXId": "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e",
          "partTypeInformation": {
            "manufacturerPartId": "ZX-55",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model A"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e",
          "sites": [
            {
              "functionValidUntil": "2025-02-08T04:30:48.000Z",
              "function": "production",
              "functionValidFrom": "2019-08-21T02:10:36.000Z",
              "catenaXSiteId": "BPNS00000003AYRE"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:0733946c-59c6-41ae-9570-cb43a6e4c79e",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "catenaXId": "urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c",
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:litre"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY1",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c",
      "bpnl": "BPNL00ARBITRARY1",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2016-12-09T05:06:53.000Z",
            "validTo": "2027-09-16T00:32:51.000Z"
          },
          "catenaXId": "urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c",
          "partTypeInformation": {
            "manufacturerPartId": "38049661-08",
            "classification": "product",
            "nameAtManufacturer": "OEM A High Voltage Battery"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c",
          "sites": [
            {
              "functionValidUntil": "2029-03-01T06:04:34.000Z",
              "function": "production",
              "functionValidFrom": "2018-02-05T09:47:59.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY1"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:aad27ddb-43aa-4e42-98c2-01e529ef127c",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 6,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY2",
              "catenaXId": "urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca97"
            },
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 6,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000003B6LU",
              "catenaXId": "urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7",
      "bpnl": "BPNL00000003B6LU",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-04-01T00:18:40.000Z",
            "validTo": "2025-07-06T08:58:34.000Z"
          },
          "catenaXId": "urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7",
          "partTypeInformation": {
            "manufacturerPartId": "32494586-73",
            "classification": "product",
            "nameAtManufacturer": "Tier A Gearbox"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7",
          "sites": [
            {
              "functionValidUntil": "2031-11-21T01:59:04.000Z",
              "function": "production",
              "functionValidFrom": "2013-09-04T06:14:54.000Z",
              "catenaXSiteId": "BPNS00000003B6LU"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:2c57b0e9-a653-411d-bdcd-64787e9fd3a7",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY3",
              "catenaXId": "urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc",
      "bpnl": "BPNL00ARBITRARY3",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2015-03-15T17:25:20.000Z",
            "validTo": "2027-12-12T17:07:52.000Z"
          },
          "catenaXId": "urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc",
          "partTypeInformation": {
            "manufacturerPartId": "6740244-02",
            "classification": "product",
            "nameAtManufacturer": "Sub Tier A Sensor"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc",
          "sites": [
            {
              "functionValidUntil": "2026-01-07T15:36:31.000Z",
              "function": "production",
              "functionValidFrom": "2015-04-29T17:11:59.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY3"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:bee5614f-9e46-4c98-9209-61a6f2b2a7fc",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca97",
      "bpnl": "BPNL00000003B3NX",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2014-03-22T06:27:39.000Z",
            "validTo": "2027-02-12T02:27:05.000Z"
          },
          "catenaXId": "urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca97",
          "partTypeInformation": {
            "manufacturerPartId": "8840838-04",
            "classification": "product",
            "nameAtManufacturer": "HV Modul"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca97",
          "sites": [
            {
              "functionValidUntil": "2025-10-14T02:16:37.000Z",
              "function": "production",
              "functionValidFrom": "2014-07-09T06:57:44.000Z",
              "catenaXSiteId": "BPNS00000003B3NX"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:e5c96ab5-896a-482c-8761-efd74777ca97",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 10,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000003B3NX",
              "catenaXId": "urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a9"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a9",
      "bpnl": "BPNL00000003B3NX",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2015-06-20T09:16:05.000Z",
            "validTo": "2032-12-30T02:19:28.000Z"
          },
          "catenaXId": "urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a9",
          "partTypeInformation": {
            "manufacturerPartId": "8840374-09",
            "classification": "product",
            "nameAtManufacturer": "ZB ZELLE"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a9",
          "sites": [
            {
              "functionValidUntil": "2032-06-15T14:18:34.000Z",
              "function": "production",
              "functionValidFrom": "2014-12-27T02:28:17.000Z",
              "catenaXSiteId": "BPNS00000003B3NX"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:c7a2b803-f8fe-4b79-b6fc-967ce847c9a9",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000003B6LU",
              "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e",
      "bpnl": "BPNL00000003B6LU",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-04-11T05:30:04.000Z",
            "validTo": "2025-04-23T19:59:03.000Z"
          },
          "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e",
          "partTypeInformation": {
            "manufacturerPartId": "7A047C7-01",
            "classification": "product",
            "nameAtManufacturer": "N Tier A CathodeMaterial"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e",
          "sites": [
            {
              "functionValidUntil": "2026-11-05T11:21:29.000Z",
              "function": "production",
              "functionValidFrom": "2016-07-05T20:28:02.000Z",
              "catenaXSiteId": "BPNS00000003B6LU"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:68904173-ad59-4a77-8412-3e73fcafbd8b",
      "bpnl": "BPNL00000003B6LU",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-09-21T23:24:07.000Z",
            "validTo": "2026-11-22T17:35:54.000Z"
          },
          "catenaXId": "urn:uuid:68904173-ad59-4a77-8412-3e73fcafbd8b",
          "partTypeInformation": {
            "manufacturerPartId": "FJ-87",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model B"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:68904173-ad59-4a77-8412-3e73fcafbd8b",
          "sites": [
            {
              "functionValidUntil": "2025-03-23T05:10:13.000Z",
              "function": "production",
              "functionValidFrom": "2018-12-09T05:11:28.000Z",
              "catenaXSiteId": "BPNS00000003B6LU"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:68904173-ad59-4a77-8412-3e73fcafbd8b",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000004FAIL",
              "catenaXId": "urn:uuid:e3e2a4d8-58bc-4ae9-afa2-e8946fda1f77"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:e3e2a4d8-58bc-4ae9-afa2-e8946fda1f77",
      "bpnl": "BPNL00000004FAIL",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2015-01-16T14:23:33.000Z",
            "validTo": "2032-05-18T15:14:36.000Z"
          },
          "catenaXId": "urn:uuid:e3e2a4d8-58bc-4ae9-afa2-e8946fda1f77",
          "partTypeInformation": {
            "manufacturerPartId": "39478586-36",
            "classification": "product",
            "nameAtManufacturer": "Tier B ECU2"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:e3e2a4d8-58bc-4ae9-afa2-e8946fda1f77",
          "sites": [
            {
              "functionValidUntil": "2024-06-08T03:49:26.000Z",
              "function": "production",
              "functionValidFrom": "2015-08-29T08:43:40.000Z",
              "catenaXSiteId": "BPNS00000004FAIL"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:e3e2a4d8-58bc-4ae9-afa2-e8946fda1f77",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000003AXS3",
              "catenaXId": "urn:uuid:b1d46d01-e308-4bd3-863a-331e64751d76"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:b1d46d01-e308-4bd3-863a-331e64751d76",
      "bpnl": "BPNL00000003AXS3",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2014-07-30T07:17:16.000Z",
            "validTo": "2028-03-27T22:34:57.000Z"
          },
          "catenaXId": "urn:uuid:b1d46d01-e308-4bd3-863a-331e64751d76",
          "partTypeInformation": {
            "manufacturerPartId": "6775244-06",
            "classification": "product",
            "nameAtManufacturer": "Sub Tier B Glue"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:b1d46d01-e308-4bd3-863a-331e64751d76",
          "sites": [
            {
              "functionValidUntil": "2026-03-11T19:58:28.000Z",
              "function": "production",
              "functionValidFrom": "2020-03-04T21:09:12.000Z",
              "catenaXSiteId": "BPNS00000003AXS3"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:b1d46d01-e308-4bd3-863a-331e64751d76",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:1c7a25ea-0490-4944-b9c9-d8c666d47958",
      "bpnl": "BPNL00ARBITRARY4",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2019-04-04T03:19:03.000Z",
            "validTo": "2024-12-29T10:25:12.000Z"
          },
          "catenaXId": "urn:uuid:1c7a25ea-0490-4944-b9c9-d8c666d47958",
          "partTypeInformation": {
            "manufacturerPartId": "ZX-55",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model A"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:1c7a25ea-0490-4944-b9c9-d8c666d47958",
          "sites": [
            {
              "functionValidUntil": "2025-02-08T04:30:48.000Z",
              "function": "production",
              "functionValidFrom": "2019-08-21T02:10:36.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY4"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:1c7a25ea-0490-4944-b9c9-d8c666d47958",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY5",
              "catenaXId": "urn:uuid:2190e4b8-63ee-411d-bf57-6cdc495bc1df"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:2190e4b8-63ee-411d-bf57-6cdc495bc1df",
      "bpnl": "BPNL00ARBITRARY5",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2016-12-09T05:06:53.000Z",
            "validTo": "2027-09-16T00:32:51.000Z"
          },
          "catenaXId": "urn:uuid:2190e4b8-63ee-411d-bf57-6cdc495bc1df",
          "partTypeInformation": {
            "manufacturerPartId": "38049661-08",
            "classification": "product",
            "nameAtManufacturer": "OEM A High Voltage Battery"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:2190e4b8-63ee-411d-bf57-6cdc495bc1df",
          "sites": [
            {
              "functionValidUntil": "2029-03-01T06:04:34.000Z",
              "function": "production",
              "functionValidFrom": "2018-02-05T09:47:59.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY5"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:2190e4b8-63ee-411d-bf57-6cdc495bc1df",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 6,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY6",
              "catenaXId": "urn:uuid:3d61ada2-1a50-42a0-b411-40a932dd56cc"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:3d61ada2-1a50-42a0-b411-40a932dd56cc",
      "bpnl": "BPNL00ARBITRARY6",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2014-03-22T06:27:39.000Z",
            "validTo": "2027-02-12T02:27:05.000Z"
          },
          "catenaXId": "urn:uuid:3d61ada2-1a50-42a0-b411-40a932dd56cc",
          "partTypeInformation": {
            "manufacturerPartId": "8840838-04",
            "classification": "product",
            "nameAtManufacturer": "HV Modul"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:3d61ada2-1a50-42a0-b411-40a932dd56cc",
          "sites": [
            {
              "functionValidUntil": "2025-10-14T02:16:37.000Z",
              "function": "production",
              "functionValidFrom": "2014-07-09T06:57:44.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY6"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:3d61ada2-1a50-42a0-b411-40a932dd56cc",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 10,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY7",
              "catenaXId": "urn:uuid:52207a60-e541-4bea-8ec4-3172f09e6dbb"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:52207a60-e541-4bea-8ec4-3172f09e6dbb",
      "bpnl": "BPNL00ARBITRARY7",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2015-06-20T09:16:05.000Z",
            "validTo": "2032-12-30T02:19:28.000Z"
          },
          "catenaXId": "urn:uuid:52207a60-e541-4bea-8ec4-3172f09e6dbb",
          "partTypeInformation": {
            "manufacturerPartId": "8840374-09",
            "classification": "product",
            "nameAtManufacturer": "ZB ZELLE"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:52207a60-e541-4bea-8ec4-3172f09e6dbb",
          "sites": [
            {
              "functionValidUntil": "2032-06-15T14:18:34.000Z",
              "function": "production",
              "functionValidFrom": "2014-12-27T02:28:17.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY7"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:52207a60-e541-4bea-8ec4-3172f09e6dbb",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000003B6LU",
              "catenaXId": "urn:uuid:86f69643-3b90-4e34-90bf-789edcf40e7e"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:3a2a1ca9-c6c1-49c7-a7ae-1dfc5fb9881f",
      "bpnl": "BPNL00ARBITRARY8",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-09-21T23:24:07.000Z",
            "validTo": "2026-11-22T17:35:54.000Z"
          },
          "catenaXId": "urn:uuid:3a2a1ca9-c6c1-49c7-a7ae-1dfc5fb9881f",
          "partTypeInformation": {
            "manufacturerPartId": "FJ-87",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model D"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:3a2a1ca9-c6c1-49c7-a7ae-1dfc5fb9881f",
          "sites": [
            {
              "functionValidUntil": "2025-03-23T05:10:13.000Z",
              "function": "production",
              "functionValidFrom": "2018-12-09T05:11:28.000Z",
              "catenaXSiteId": "BPNS00ARBITRARY8"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:3a2a1ca9-c6c1-49c7-a7ae-1dfc5fb9881f",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00000004FAIL",
              "catenaXId": "urn:uuid:9846f1c6-0dd0-4d5a-9c7a-30af0b7e0247"
            },
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY10",
              "catenaXId": "urn:uuid:22847bfd-eb8d-41b7-b088-3a548b7541a8"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:9846f1c6-0dd0-4d5a-9c7a-30af0b7e0247",
      "bpnl": "BPNL00000004FAIL",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2014-07-30T07:17:16.000Z",
            "validTo": "2028-03-27T22:34:57.000Z"
          },
          "catenaXId": "urn:uuid:9846f1c6-0dd0-4d5a-9c7a-30af0b7e0247",
          "partTypeInformation": {
            "manufacturerPartId": "6775244-06",
            "classification": "product",
            "nameAtManufacturer": "Sub Tier B Glue"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:9846f1c6-0dd0-4d5a-9c7a-30af0b7e0247",
          "sites": [
            {
              "functionValidUntil": "2026-03-11T19:58:28.000Z",
              "function": "production",
              "functionValidFrom": "2020-03-04T21:09:12.000Z",
              "catenaXSiteId": "BPNS00000004FAIL"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:22847bfd-eb8d-41b7-b088-3a548b7541a8",
      "bpnl": "BPNL0ARBITRARY10",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2015-01-16T14:23:33.000Z",
            "validTo": "2032-05-18T15:14:36.000Z"
          },
          "catenaXId": "urn:uuid:22847bfd-eb8d-41b7-b088-3a548b7541a8",
          "partTypeInformation": {
            "manufacturerPartId": "39478586-36",
            "classification": "product",
            "nameAtManufacturer": "Tier B ECU2"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:22847bfd-eb8d-41b7-b088-3a548b7541a8",
          "sites": [
            {
              "functionValidUntil": "2024-06-08T03:49:26.000Z",
              "function": "production",
              "functionValidFrom": "2015-08-29T08:43:40.000Z",
              "catenaXSiteId": "BPNS0ARBITRARY10"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:22847bfd-eb8d-41b7-b088-3a548b7541a8",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNS00ARBITRARY11",
              "catenaXId": "urn:uuid:01796f87-9677-43f0-9c62-61665be29d85"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:01796f87-9677-43f0-9c62-61665be29d85",
      "bpnl": "BPNL0ARBITRARY11",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2014-07-30T07:17:16.000Z",
            "validTo": "2028-03-27T22:34:57.000Z"
          },
          "catenaXId": "urn:uuid:01796f87-9677-43f0-9c62-61665be29d85",
          "partTypeInformation": {
            "manufacturerPartId": "6775244-06",
            "classification": "product",
            "nameAtManufacturer": "Sub Tier B Glue"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:01796f87-9677-43f0-9c62-61665be29d85",
          "sites": [
            {
              "functionValidUntil": "2026-03-11T19:58:28.000Z",
              "function": "production",
              "functionValidFrom": "2020-03-04T21:09:12.000Z",
              "catenaXSiteId": "BPNS0ARBITRARY11"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:b0faace3-d41f-45b8-9573-175a33efbaaf",
      "bpnl": "BPNL00ARBITRARY8",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-09-21T23:24:07.000Z",
            "validTo": "2026-11-22T17:35:54.000Z"
          },
          "catenaXId": "urn:uuid:b0faace3-d41f-45b8-9573-175a33efbaaf",
          "partTypeInformation": {
            "manufacturerPartId": "FJ-87",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model E"
          }
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:b0faace3-d41f-45b8-9573-175a33efbaaf",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL0ARBITRARY10",
              "catenaXId": "urn:uuid:1d12d050-d388-4f3d-a880-c9ec4ac84786"
            },
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY9",
              "catenaXId": "urn:uuid:ec759282-e8fb-4a94-aafe-71585a3f2948"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:ec759282-e8fb-4a94-aafe-71585a3f2948",
      "bpnl": "BPNL00ARBITRARY9",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:ec759282-e8fb-4a94-aafe-71585a3f2948",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:1d12d050-d388-4f3d-a880-c9ec4ac84786",
      "bpnl": "BPNL0ARBITRARY10",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:1d12d050-d388-4f3d-a880-c9ec4ac84786",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL0ARBITRARY11",
              "catenaXId": "urn:uuid:b636c28a-6924-4498-88f9-5d073f16ed65"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:b636c28a-6924-4498-88f9-5d073f16ed65",
      "bpnl": "BPNL0ARBITRARY11",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:b636c28a-6924-4498-88f9-5d073f16ed65",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:f5024c70-6c4f-4ec5-b23b-aa6a91110611",
      "bpnl": "BPNL00ARBITRARY8",
      "urn:bamm:io.catenax.part_as_planned:1.0.1#PartAsPlanned": [
        {
          "validityPeriod": {
            "validFrom": "2013-09-21T23:24:07.000Z",
            "validTo": "2026-11-22T17:35:54.000Z"
          },
          "catenaXId": "urn:uuid:f5024c70-6c4f-4ec5-b23b-aa6a91110611",
          "partTypeInformation": {
            "manufacturerPartId": "FJ-87",
            "classification": "product",
            "nameAtManufacturer": "Vehicle Model F"
          }
        }
      ],
      "urn:bamm:io.catenax.part_site_information_as_planned:1.0.0#PartSiteInformationAsPlanned": [
        {
          "catenaXId": "urn:uuid:f5024c70-6c4f-4ec5-b23b-aa6a91110611",
          "sites": [
            {
              "functionValidUntil": "2025-03-23T05:10:13.000Z",
              "function": "production",
              "functionValidFrom": "2018-12-09T05:11:28.000Z"
            }
          ]
        }
      ],
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:f5024c70-6c4f-4ec5-b23b-aa6a91110611",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL0ARBITRARY10",
              "catenaXId": "urn:uuid:5d827e81-ac88-4acb-8a0e-8565ddd729b1"
            },
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL00ARBITRARY9",
              "catenaXId": "urn:uuid:51eeafa1-a7a5-4f2d-8c7a-15036eb43553"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:51eeafa1-a7a5-4f2d-8c7a-15036eb43553",
      "bpnl": "BPNL00ARBITRARY9",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:51eeafa1-a7a5-4f2d-8c7a-15036eb43553",
          "childItems": []
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:5d827e81-ac88-4acb-8a0e-8565ddd729b1",
      "bpnl": "BPNL0ARBITRARY10",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:5d827e81-ac88-4acb-8a0e-8565ddd729b1",
          "childItems": [
            {
              "validityPeriod": {
                "validFrom": "2023-03-21T08:17:29.187+01:00",
                "validTo": "2024-07-01T16:10:00.000+01:00"
              },
              "quantity": {
                "quantityNumber": 1,
                "measurementUnit": "unit:piece"
              },
              "createdOn": "2022-02-03T14:48:54.709Z",
              "lastModifiedOn": "2022-02-03T14:48:54.709Z",
              "businessPartner": "BPNL0ARBITRARY11",
              "catenaXId": "urn:uuid:31557090-b504-41a2-8f13-e0e2c2c16932"
            }
          ]
        }
      ]
    },
    {
      "catenaXId": "urn:uuid:31557090-b504-41a2-8f13-e0e2c2c16932",
      "bpnl": "BPNL0ARBITRARY11",
      "urn:bamm:io.catenax.single_level_bom_as_planned:2.0.0#SingleLevelBomAsPlanned": [
        {
          "catenaXId": "urn:uuid:31557090-b504-41a2-8f13-e0e2c2c16932",
          "childItems": []
        }
      ]
    }
  ]
}];
