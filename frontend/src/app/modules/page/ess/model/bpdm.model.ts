/********************************************************************************
 * Copyright (c) 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { PaginationBpdmResponse } from '@core/model/pagination.bpdm.model';
import { PaginationResponse } from '@core/model/pagination.model';

export interface LegalAddress {
  bpna: string,
  name: string,
  alternativePostalAddress: string,
  bpnLegalEntity: string,
  isLegalAddress: boolean,
  bpnSite: string,
  isMainAddress: boolean,
  createdAt: string,
  updatedAt: string
}

export interface LegalEntity {
  score: number,
  legalName: string,
  bpnl: string,
  legalShortName: string,
  currentness: string,
  createdAt: string,
  updatedAt: string,
  legalAddress: LegalAddress
}

export interface LegalEntityResponse {
  score: number,
  legalName: string,
  bpnl: string,
  legalShortName: string,
  currentness: string,
  createdAt: string,
  updatedAt: string,
  legalAddress: LegalAddress
}

export type LegalEntitiesResponse = PaginationBpdmResponse<LegalEntityResponse>;

export const CountryCodes = [
  {
    "key2": "AC",
    "key3": "ASC",
    "name": "Ascension Island"
  },{
    "key2": "AD",
    "key3": "AND",
    "name": "Andorra"
  },{
    "key2": "AE",
    "key3": "ARE",
    "name": "United Arab Emirates"
  },{
    "key2": "AF",
    "key3": "AFG",
    "name": "Afghanistan"
  },{
    "key2": "AG",
    "key3": "ATG",
    "name": "Antigua and Barbuda"
  },{
    "key2": "AI",
    "key3": "AIA",
    "name": "Anguilla"
  },{
    "key2": "AL",
    "key3": "ALB",
    "name": "Albania"
  },{
    "key2": "AM",
    "key3": "ARM",
    "name": "Armenia"
  },{
    "key2": "AN",
    "key3": "ANT",
    "name": "Netherlands Antilles"
  },{
    "key2": "AO",
    "key3": "AGO",
    "name": "Angola"
  },{
    "key2": "AQ",
    "key3": "ATA",
    "name": "Antarctica"
  },{
    "key2": "AR",
    "key3": "ARG",
    "name": "Argentina"
  },{
    "key2": "AS",
    "key3": "ASM",
    "name": "American Samoa"
  },{
    "key2": "AT",
    "key3": "AUT",
    "name": "Austria"
  },{
    "key2": "AU",
    "key3": "AUS",
    "name": "Australia"
  },{
    "key2": "AW",
    "key3": "ABW",
    "name": "Aruba"
  },{
    "key2": "AX",
    "key3": "ALA",
    "name": "Åland Islands"
  },{
    "key2": "AZ",
    "key3": "AZE",
    "name": "Azerbaijan"
  },{
    "key2": "BA",
    "key3": "BIH",
    "name": "Bosnia and Herzegovina"
  },{
    "key2": "BB",
    "key3": "BRB",
    "name": "Barbados"
  },{
    "key2": "BD",
    "key3": "BGD",
    "name": "Bangladesh"
  },{
    "key2": "BE",
    "key3": "BEL",
    "name": "Belgium"
  },{
    "key2": "BF",
    "key3": "BFA",
    "name": "Burkina Faso"
  },{
    "key2": "BG",
    "key3": "BGR",
    "name": "Bulgaria"
  },{
    "key2": "BH",
    "key3": "BHR",
    "name": "Bahrain"
  },{
    "key2": "BI",
    "key3": "BDI",
    "name": "Burundi"
  },{
    "key2": "BJ",
    "key3": "BEN",
    "name": "Benin"
  },{
    "key2": "BL",
    "key3": "BLM",
    "name": "Saint Barthélemy"
  },{
    "key2": "BM",
    "key3": "BMU",
    "name": "Bermuda"
  },{
    "key2": "BN",
    "key3": "BRN",
    "name": "Brunei Darussalam"
  },{
    "key2": "BO",
    "key3": "BOL",
    "name": "Bolivia, Plurinational State of"
  },{
    "key2": "BQ",
    "key3": "BES",
    "name": "Bonaire, Sint Eustatius and Saba"
  },{
    "key2": "BR",
    "key3": "BRA",
    "name": "Brazil"
  },{
    "key2": "BS",
    "key3": "BHS",
    "name": "Bahamas"
  },{
    "key2": "BT",
    "key3": "BTN",
    "name": "Bhutan"
  },{
    "key2": "BU",
    "key3": "BUR",
    "name": "Burma"
  },{
    "key2": "BV",
    "key3": "BVT",
    "name": "Bouvet Island"
  },{
    "key2": "BW",
    "key3": "BWA",
    "name": "Botswana"
  },{
    "key2": "BY",
    "key3": "BLR",
    "name": "Belarus"
  },{
    "key2": "BZ",
    "key3": "BLZ",
    "name": "Belize"
  },{
    "key2": "CA",
    "key3": "CAN",
    "name": "Canada"
  },{
    "key2": "CC",
    "key3": "CCK",
    "name": "Cocos (Keeling) Islands"
  },{
    "key2": "CD",
    "key3": "COD",
    "name": "Congo, the Democratic Republic of the"
  },{
    "key2": "CF",
    "key3": "CAF",
    "name": "Central African Republic"
  },{
    "key2": "CG",
    "key3": "COG",
    "name": "Congo"
  },{
    "key2": "CH",
    "key3": "CHE",
    "name": "Switzerland"
  },{
    "key2": "CI",
    "key3": "CIV",
    "name": "Côte d'Ivoire"
  },{
    "key2": "CK",
    "key3": "COK",
    "name": "Cook Islands"
  },{
    "key2": "CL",
    "key3": "CHL",
    "name": "Chile"
  },{
    "key2": "CM",
    "key3": "CMR",
    "name": "Cameroon"
  },{
    "key2": "CN",
    "key3": "CHN",
    "name": "China"
  },{
    "key2": "CO",
    "key3": "COL",
    "name": "Colombia"
  },{
    "key2": "CP",
    "key3": "CPT",
    "name": "Clipperton Island"
  },{
    "key2": "CR",
    "key3": "CRI",
    "name": "Costa Rica"
  },{
    "key2": "CS",
    "key3": "SCG",
    "name": "Serbia and Montenegro"
  },{
    "key2": "CU",
    "key3": "CUB",
    "name": "Cuba"
  },{
    "key2": "CV",
    "key3": "CPV",
    "name": "Cape Verde"
  },{
    "key2": "CW",
    "key3": "CUW",
    "name": "Curaçao"
  },{
    "key2": "CX",
    "key3": "CXR",
    "name": "Christmas Island"
  },{
    "key2": "CY",
    "key3": "CYP",
    "name": "Cyprus"
  },{
    "key2": "CZ",
    "key3": "CZE",
    "name": "Czech Republic"
  },{
    "key2": "DE",
    "key3": "DEU",
    "name": "Germany"
  },{
    "key2": "DG",
    "key3": "DGA",
    "name": "Diego Garcia"
  },{
    "key2": "DJ",
    "key3": "DJI",
    "name": "Djibouti"
  },{
    "key2": "DK",
    "key3": "DNK",
    "name": "Denmark"
  },{
    "key2": "DM",
    "key3": "DMA",
    "name": "Dominica"
  },{
    "key2": "DO",
    "key3": "DOM",
    "name": "Dominican Republic"
  },{
    "key2": "DZ",
    "key3": "DZA",
    "name": "Algeria"
  },{
    "key2": "EA",
    "key3": "null",
    "name": "Ceuta, Melilla"
  },{
    "key2": "EC",
    "key3": "ECU",
    "name": "Ecuador"
  },{
    "key2": "EE",
    "key3": "EST",
    "name": "Estonia"
  },{
    "key2": "EG",
    "key3": "EGY",
    "name": "Egypt"
  },{
    "key2": "EH",
    "key3": "ESH",
    "name": "Western Sahara"
  },{
    "key2": "ER",
    "key3": "ERI",
    "name": "Eritrea"
  },{
    "key2": "ES",
    "key3": "ESP",
    "name": "Spain"
  },{
    "key2": "ET",
    "key3": "ETH",
    "name": "Ethiopia"
  },{
    "key2": "EU",
    "key3": "null",
    "name": "European Union"
  },{
    "key2": "EZ",
    "key3": "null",
    "name": "Eurozone"
  },{
    "key2": "FI",
    "key3": "FIN",
    "name": "Finland"
  },{
    "key2": "FJ",
    "key3": "FJI",
    "name": "Fiji"
  },{
    "key2": "FK",
    "key3": "FLK",
    "name": "Falkland Islands (Malvinas)"
  },{
    "key2": "FM",
    "key3": "FSM",
    "name": "Micronesia, Federated States of"
  },{
    "key2": "FO",
    "key3": "FRO",
    "name": "Faroe Islands"
  },{
    "key2": "FR",
    "key3": "FRA",
    "name": "France"
  },{
    "key2": "FX",
    "key3": "FXX",
    "name": "France, Metropolitan"
  },{
    "key2": "GA",
    "key3": "GAB",
    "name": "Gabon"
  },{
    "key2": "GB",
    "key3": "GBR",
    "name": "United Kingdom"
  },{
    "key2": "GD",
    "key3": "GRD",
    "name": "Grenada"
  },{
    "key2": "GE",
    "key3": "GEO",
    "name": "Georgia"
  },{
    "key2": "GF",
    "key3": "GUF",
    "name": "French Guiana"
  },{
    "key2": "GG",
    "key3": "GGY",
    "name": "Guernsey"
  },{
    "key2": "GH",
    "key3": "GHA",
    "name": "Ghana"
  },{
    "key2": "GI",
    "key3": "GIB",
    "name": "Gibraltar"
  },{
    "key2": "GL",
    "key3": "GRL",
    "name": "Greenland"
  },{
    "key2": "GM",
    "key3": "GMB",
    "name": "Gambia"
  },{
    "key2": "GN",
    "key3": "GIN",
    "name": "Guinea"
  },{
    "key2": "GP",
    "key3": "GLP",
    "name": "Guadeloupe"
  },{
    "key2": "GQ",
    "key3": "GNQ",
    "name": "Equatorial Guinea"
  },{
    "key2": "GR",
    "key3": "GRC",
    "name": "Greece"
  },{
    "key2": "GS",
    "key3": "SGS",
    "name": "South Georgia and the South Sandwich Islands"
  },{
    "key2": "GT",
    "key3": "GTM",
    "name": "Guatemala"
  },{
    "key2": "GU",
    "key3": "GUM",
    "name": "Guam"
  },{
    "key2": "GW",
    "key3": "GNB",
    "name": "Guinea-Bissau"
  },{
    "key2": "GY",
    "key3": "GUY",
    "name": "Guyana"
  },{
    "key2": "HK",
    "key3": "HKG",
    "name": "Hong Kong"
  },{
    "key2": "HM",
    "key3": "HMD",
    "name": "Heard Island and McDonald Islands"
  },{
    "key2": "HN",
    "key3": "HND",
    "name": "Honduras"
  },{
    "key2": "HR",
    "key3": "HRV",
    "name": "Croatia"
  },{
    "key2": "HT",
    "key3": "HTI",
    "name": "Haiti"
  },{
    "key2": "HU",
    "key3": "HUN",
    "name": "Hungary"
  },{
    "key2": "IC",
    "key3": "null",
    "name": "Canary Islands"
  },{
    "key2": "ID",
    "key3": "IDN",
    "name": "Indonesia"
  },{
    "key2": "IE",
    "key3": "IRL",
    "name": "Ireland"
  },{
    "key2": "IL",
    "key3": "ISR",
    "name": "Israel"
  },{
    "key2": "IM",
    "key3": "IMN",
    "name": "Isle of Man"
  },{
    "key2": "IN",
    "key3": "IND",
    "name": "India"
  },{
    "key2": "IO",
    "key3": "IOT",
    "name": "British Indian Ocean Territory"
  },{
    "key2": "IQ",
    "key3": "IRQ",
    "name": "Iraq"
  },{
    "key2": "IR",
    "key3": "IRN",
    "name": "Iran, Islamic Republic of"
  },{
    "key2": "IS",
    "key3": "ISL",
    "name": "Iceland"
  },{
    "key2": "IT",
    "key3": "ITA",
    "name": "Italy"
  },{
    "key2": "JE",
    "key3": "JEY",
    "name": "Jersey"
  },{
    "key2": "JM",
    "key3": "JAM",
    "name": "Jamaica"
  },{
    "key2": "JO",
    "key3": "JOR",
    "name": "Jordan"
  },{
    "key2": "JP",
    "key3": "JPN",
    "name": "Japan"
  },{
    "key2": "KE",
    "key3": "KEN",
    "name": "Kenya"
  },{
    "key2": "KG",
    "key3": "KGZ",
    "name": "Kyrgyzstan"
  },{
    "key2": "KH",
    "key3": "KHM",
    "name": "Cambodia"
  },{
    "key2": "KI",
    "key3": "KIR",
    "name": "Kiribati"
  },{
    "key2": "KM",
    "key3": "COM",
    "name": "Comoros"
  },{
    "key2": "KN",
    "key3": "KNA",
    "name": "Saint Kitts and Nevis"
  },{
    "key2": "KP",
    "key3": "PRK",
    "name": "Korea, Democratic People's Republic of"
  },{
    "key2": "KR",
    "key3": "KOR",
    "name": "Korea, Republic of"
  },{
    "key2": "KW",
    "key3": "KWT",
    "name": "Kuwait"
  },{
    "key2": "KY",
    "key3": "CYM",
    "name": "Cayman Islands"
  },{
    "key2": "KZ",
    "key3": "KAZ",
    "name": "Kazakhstan"
  },{
    "key2": "LA",
    "key3": "LAO",
    "name": "Lao People's Democratic Republic"
  },{
    "key2": "LB",
    "key3": "LBN",
    "name": "Lebanon"
  },{
    "key2": "LC",
    "key3": "LCA",
    "name": "Saint Lucia"
  },{
    "key2": "LI",
    "key3": "LIE",
    "name": "Liechtenstein"
  },{
    "key2": "LK",
    "key3": "LKA",
    "name": "Sri Lanka"
  },{
    "key2": "LR",
    "key3": "LBR",
    "name": "Liberia"
  },{
    "key2": "LS",
    "key3": "LSO",
    "name": "Lesotho"
  },{
    "key2": "LT",
    "key3": "LTU",
    "name": "Lithuania"
  },{
    "key2": "LU",
    "key3": "LUX",
    "name": "Luxembourg"
  },{
    "key2": "LV",
    "key3": "LVA",
    "name": "Latvia"
  },{
    "key2": "LY",
    "key3": "LBY",
    "name": "Libya"
  },{
    "key2": "MA",
    "key3": "MAR",
    "name": "Morocco"
  },{
    "key2": "MC",
    "key3": "MCO",
    "name": "Monaco"
  },{
    "key2": "MD",
    "key3": "MDA",
    "name": "Moldova, Republic of"
  },{
    "key2": "ME",
    "key3": "MNE",
    "name": "Montenegro"
  },{
    "key2": "MF",
    "key3": "MAF",
    "name": "Saint Martin (French part)"
  },{
    "key2": "MG",
    "key3": "MDG",
    "name": "Madagascar"
  },{
    "key2": "MH",
    "key3": "MHL",
    "name": "Marshall Islands"
  },{
    "key2": "MK",
    "key3": "MKD",
    "name": "North Macedonia, Republic of"
  },{
    "key2": "ML",
    "key3": "MLI",
    "name": "Mali"
  },{
    "key2": "MM",
    "key3": "MMR",
    "name": "Myanmar"
  },{
    "key2": "MN",
    "key3": "MNG",
    "name": "Mongolia"
  },{
    "key2": "MO",
    "key3": "MAC",
    "name": "Macao"
  },{
    "key2": "MP",
    "key3": "MNP",
    "name": "Northern Mariana Islands"
  },{
    "key2": "MQ",
    "key3": "MTQ",
    "name": "Martinique"
  },{
    "key2": "MR",
    "key3": "MRT",
    "name": "Mauritania"
  },{
    "key2": "MS",
    "key3": "MSR",
    "name": "Montserrat"
  },{
    "key2": "MT",
    "key3": "MLT",
    "name": "Malta"
  },{
    "key2": "MU",
    "key3": "MUS",
    "name": "Mauritius"
  },{
    "key2": "MV",
    "key3": "MDV",
    "name": "Maldives"
  },{
    "key2": "MW",
    "key3": "MWI",
    "name": "Malawi"
  },{
    "key2": "MX",
    "key3": "MEX",
    "name": "Mexico"
  },{
    "key2": "MY",
    "key3": "MYS",
    "name": "Malaysia"
  },{
    "key2": "MZ",
    "key3": "MOZ",
    "name": "Mozambique"
  },{
    "key2": "NA",
    "key3": "NAM",
    "name": "Namibia"
  },{
    "key2": "NC",
    "key3": "NCL",
    "name": "New Caledonia"
  },{
    "key2": "NE",
    "key3": "NER",
    "name": "Niger"
  },{
    "key2": "NF",
    "key3": "NFK",
    "name": "Norfolk Island"
  },{
    "key2": "NG",
    "key3": "NGA",
    "name": "Nigeria"
  },{
    "key2": "NI",
    "key3": "NIC",
    "name": "Nicaragua"
  },{
    "key2": "NL",
    "key3": "NLD",
    "name": "Netherlands"
  },{
    "key2": "NO",
    "key3": "NOR",
    "name": "Norway"
  },{
    "key2": "NP",
    "key3": "NPL",
    "name": "Nepal"
  },{
    "key2": "NR",
    "key3": "NRU",
    "name": "Nauru"
  },{
    "key2": "NT",
    "key3": "NTZ",
    "name": "Neutral Zone"
  },{
    "key2": "NU",
    "key3": "NIU",
    "name": "Niue"
  },{
    "key2": "NZ",
    "key3": "NZL",
    "name": "New Zealand"
  },{
    "key2": "OM",
    "key3": "OMN",
    "name": "Oman"
  },{
    "key2": "PA",
    "key3": "PAN",
    "name": "Panama"
  },{
    "key2": "PE",
    "key3": "PER",
    "name": "Peru"
  },{
    "key2": "PF",
    "key3": "PYF",
    "name": "French Polynesia"
  },{
    "key2": "PG",
    "key3": "PNG",
    "name": "Papua New Guinea"
  },{
    "key2": "PH",
    "key3": "PHL",
    "name": "Philippines"
  },{
    "key2": "PK",
    "key3": "PAK",
    "name": "Pakistan"
  },{
    "key2": "PL",
    "key3": "POL",
    "name": "Poland"
  },{
    "key2": "PM",
    "key3": "SPM",
    "name": "Saint Pierre and Miquelon"
  },{
    "key2": "PN",
    "key3": "PCN",
    "name": "Pitcairn"
  },{
    "key2": "PR",
    "key3": "PRI",
    "name": "Puerto Rico"
  },{
    "key2": "PS",
    "key3": "PSE",
    "name": "Palestine, State of"
  },{
    "key2": "PT",
    "key3": "PRT",
    "name": "Portugal"
  },{
    "key2": "PW",
    "key3": "PLW",
    "name": "Palau"
  },{
    "key2": "PY",
    "key3": "PRY",
    "name": "Paraguay"
  },{
    "key2": "QA",
    "key3": "QAT",
    "name": "Qatar"
  },{
    "key2": "RE",
    "key3": "REU",
    "name": "Réunion"
  },{
    "key2": "RO",
    "key3": "ROU",
    "name": "Romania"
  },{
    "key2": "RS",
    "key3": "SRB",
    "name": "Serbia"
  },{
    "key2": "RU",
    "key3": "RUS",
    "name": "Russian Federation"
  },{
    "key2": "RW",
    "key3": "RWA",
    "name": "Rwanda"
  },{
    "key2": "SA",
    "key3": "SAU",
    "name": "Saudi Arabia"
  },{
    "key2": "SB",
    "key3": "SLB",
    "name": "Solomon Islands"
  },{
    "key2": "SC",
    "key3": "SYC",
    "name": "Seychelles"
  },{
    "key2": "SD",
    "key3": "SDN",
    "name": "Sudan"
  },{
    "key2": "SE",
    "key3": "SWE",
    "name": "Sweden"
  },{
    "key2": "SF",
    "key3": "FIN",
    "name": "Finland"
  },{
    "key2": "SG",
    "key3": "SGP",
    "name": "Singapore"
  },{
    "key2": "SH",
    "key3": "SHN",
    "name": "Saint Helena, Ascension and Tristan da Cunha"
  },{
    "key2": "SI",
    "key3": "SVN",
    "name": "Slovenia"
  },{
    "key2": "SJ",
    "key3": "SJM",
    "name": "Svalbard and Jan Mayen"
  },{
    "key2": "SK",
    "key3": "SVK",
    "name": "Slovakia"
  },{
    "key2": "SL",
    "key3": "SLE",
    "name": "Sierra Leone"
  },{
    "key2": "SM",
    "key3": "SMR",
    "name": "San Marino"
  },{
    "key2": "SN",
    "key3": "SEN",
    "name": "Senegal"
  },{
    "key2": "SO",
    "key3": "SOM",
    "name": "Somalia"
  },{
    "key2": "SR",
    "key3": "SUR",
    "name": "Suriname"
  },{
    "key2": "SS",
    "key3": "SSD",
    "name": "South Sudan"
  },{
    "key2": "ST",
    "key3": "STP",
    "name": "Sao Tome and Principe"
  },{
    "key2": "SU",
    "key3": "SUN",
    "name": "USSR"
  },{
    "key2": "SV",
    "key3": "SLV",
    "name": "El Salvador"
  },{
    "key2": "SX",
    "key3": "SXM",
    "name": "Sint Maarten (Dutch part)"
  },{
    "key2": "SY",
    "key3": "SYR",
    "name": "Syrian Arab Republic"
  },{
    "key2": "SZ",
    "key3": "SWZ",
    "name": "Eswatini"
  },{
    "key2": "TA",
    "key3": "TAA",
    "name": "Tristan da Cunha"
  },{
    "key2": "TC",
    "key3": "TCA",
    "name": "Turks and Caicos Islands"
  },{
    "key2": "TD",
    "key3": "TCD",
    "name": "Chad"
  },{
    "key2": "TF",
    "key3": "ATF",
    "name": "French Southern Territories"
  },{
    "key2": "TG",
    "key3": "TGO",
    "name": "Togo"
  },{
    "key2": "TH",
    "key3": "THA",
    "name": "Thailand"
  },{
    "key2": "TJ",
    "key3": "TJK",
    "name": "Tajikistan"
  },{
    "key2": "TK",
    "key3": "TKL",
    "name": "Tokelau"
  },{
    "key2": "TL",
    "key3": "TLS",
    "name": "Timor-Leste"
  },{
    "key2": "TM",
    "key3": "TKM",
    "name": "Turkmenistan"
  },{
    "key2": "TN",
    "key3": "TUN",
    "name": "Tunisia"
  },{
    "key2": "TO",
    "key3": "TON",
    "name": "Tonga"
  },{
    "key2": "TP",
    "key3": "TMP",
    "name": "East Timor"
  },{
    "key2": "TR",
    "key3": "TUR",
    "name": "Turkey"
  },{
    "key2": "TT",
    "key3": "TTO",
    "name": "Trinidad and Tobago"
  },{
    "key2": "TV",
    "key3": "TUV",
    "name": "Tuvalu"
  },{
    "key2": "TW",
    "key3": "TWN",
    "name": "Taiwan, Province of China"
  },{
    "key2": "TZ",
    "key3": "TZA",
    "name": "Tanzania, United Republic of"
  },{
    "key2": "UA",
    "key3": "UKR",
    "name": "Ukraine"
  },{
    "key2": "UG",
    "key3": "UGA",
    "name": "Uganda"
  },{
    "key2": "UK",
    "key3": "null",
    "name": "United Kingdom"
  },{
    "key2": "UM",
    "key3": "UMI",
    "name": "United States Minor Outlying Islands"
  },{
    "key2": "US",
    "key3": "USA",
    "name": "United States"
  },{
    "key2": "UY",
    "key3": "URY",
    "name": "Uruguay"
  },{
    "key2": "UZ",
    "key3": "UZB",
    "name": "Uzbekistan"
  },{
    "key2": "VA",
    "key3": "VAT",
    "name": "Holy See (Vatican City State)"
  },{
    "key2": "VC",
    "key3": "VCT",
    "name": "Saint Vincent and the Grenadines"
  },{
    "key2": "VE",
    "key3": "VEN",
    "name": "Venezuela, Bolivarian Republic of"
  },{
    "key2": "VG",
    "key3": "VGB",
    "name": "Virgin Islands, British"
  },{
    "key2": "VI",
    "key3": "VIR",
    "name": "Virgin Islands, U.S."
  },{
    "key2": "VN",
    "key3": "VNM",
    "name": "Viet Nam"
  },{
    "key2": "VU",
    "key3": "VUT",
    "name": "Vanuatu"
  },{
    "key2": "WF",
    "key3": "WLF",
    "name": "Wallis and Futuna"
  },{
    "key2": "WS",
    "key3": "WSM",
    "name": "Samoa"
  },{
    "key2": "XI",
    "key3": "XXI",
    "name": "Northern Ireland"
  },{
    "key2": "XU",
    "key3": "XXU",
    "name": "United Kingdom (excluding Northern Ireland)"
  },{
    "key2": "XK",
    "key3": "XKX",
    "name": "Kosovo, Republic of"
  },{
    "key2": "YE",
    "key3": "YEM",
    "name": "Yemen"
  },{
    "key2": "YT",
    "key3": "MYT",
    "name": "Mayotte"
  },{
    "key2": "YU",
    "key3": "YUG",
    "name": "Yugoslavia"
  },{
    "key2": "ZA",
    "key3": "ZAF",
    "name": "South Africa"
  },{
    "key2": "ZM",
    "key3": "ZMB",
    "name": "Zambia"
  },{
    "key2": "ZR",
    "key3": "ZAR",
    "name": "Zaire"
  },{
    "key2": "ZW",
    "key3": "ZWE",
    "name": "Zimbabwe"
  }
]
