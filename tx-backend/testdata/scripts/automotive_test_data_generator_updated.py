#!/usr/bin/env python3
"""
********************************************************************************
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************
"""
"""
Automotive Test Data Generator - CX-0127 Compliant

Generates realistic automotive part hierarchy test data in the format expected by
Eclipse Tractus-X Item Relationship Service (IRS) and Trace-X, compliant with
CX-0127 Industry Core Part Instance standards.

Usage:
    python automotive_test_data_generator.py BPNL1 BPNL2 NUM_VEHICLES

Example:
    python automotive_test_data_generator.py BPNL000000002OEM BPNL000000002T1S 100
"""

import json
import uuid
import random
import argparse
from datetime import datetime, timedelta
from typing import List, Dict, Any, Tuple

class AutomotivePartGenerator:
    def __init__(self, bpnl_oem: str, bpnl_tier1: str):
        self.bpnl_oem = bpnl_oem
        self.bpnl_tier1 = bpnl_tier1
        self.bpns_oem = bpnl_oem.replace('BPNL', 'BPNS')
        self.bpns_tier1 = bpnl_tier1.replace('BPNL', 'BPNS')

        # Base date for manufacturing (yesterday for vehicles)
        self.base_date = datetime.now() - timedelta(days=1)

        # Part definitions with realistic automotive components
        self.oem_parts = [
            {
                "name": "Body Control Module",
                "part_id": "BCM-2024",
                "gin_classification": "1004712",
                "oem_classification": "Electronic Control Unit",
                "oem_description": "Body electronics control system"
            },
            {
                "name": "Instrument Cluster",
                "part_id": "IC-7890",
                "gin_classification": "1004714",
                "oem_classification": "Display Unit",
                "oem_description": "Driver information display system"
            },
            {
                "name": "Central Gateway",
                "part_id": "CGW-4567",
                "gin_classification": "1004715",
                "oem_classification": "Network Gateway",
                "oem_description": "Vehicle network communication hub"
            },
            {
                "name": "Seat Control Unit",
                "part_id": "SCU-3456",
                "gin_classification": "1004716",
                "oem_classification": "Comfort Control",
                "oem_description": "Seat adjustment and memory system"
            },
            {
                "name": "Climate Control Unit",
                "part_id": "CCU-6789",
                "gin_classification": "1004717",
                "oem_classification": "HVAC Control",
                "oem_description": "Climate control management system"
            }
        ]

        self.tier1_parts = [
            {
                "name": "Engine Control Unit",
                "part_id": "ECU-1001",
                "gin_classification": "1004718",
                "oem_classification": "Powertrain Control",
                "oem_description": "Engine management system"
            },
            {
                "name": "Transmission Control Unit",
                "part_id": "TCU-1002",
                "gin_classification": "1004719",
                "oem_classification": "Powertrain Control",
                "oem_description": "Transmission control system"
            },
            {
                "name": "Anti-lock Braking System",
                "part_id": "ABS-2001",
                "gin_classification": "1004720",
                "oem_classification": "Safety System",
                "oem_description": "Anti-lock brake control system"
            },
            {
                "name": "Electronic Stability Control",
                "part_id": "ESC-2002",
                "gin_classification": "1004721",
                "oem_classification": "Safety System",
                "oem_description": "Vehicle stability management"
            },
            {
                "name": "Airbag Control Unit",
                "part_id": "ACU-2003",
                "gin_classification": "1004722",
                "oem_classification": "Safety System",
                "oem_description": "Airbag deployment control"
            },
            {
                "name": "Power Steering Control",
                "part_id": "PSC-3001",
                "gin_classification": "1004723",
                "oem_classification": "Steering System",
                "oem_description": "Electric power steering control"
            },
            {
                "name": "Battery Management System",
                "part_id": "BMS-4001",
                "gin_classification": "1004713",
                "oem_classification": "Traction Battery",
                "oem_description": "High voltage battery management"
            },
            {
                "name": "Charging Control Unit",
                "part_id": "CCU-4002",
                "gin_classification": "1004724",
                "oem_classification": "Energy Management",
                "oem_description": "EV charging system control"
            },
            {
                "name": "DC-DC Converter",
                "part_id": "DCDC-4003",
                "gin_classification": "1004725",
                "oem_classification": "Power Electronics",
                "oem_description": "High to low voltage converter"
            },
            {
                "name": "Onboard Charger",
                "part_id": "OBC-4004",
                "gin_classification": "1004726",
                "oem_classification": "Charging System",
                "oem_description": "AC to DC charging converter"
            },
            {
                "name": "High Voltage Junction Box",
                "part_id": "HVJB-5001",
                "gin_classification": "1004727",
                "oem_classification": "Electrical Distribution",
                "oem_description": "High voltage power distribution"
            },
            {
                "name": "Telematics Control Unit",
                "part_id": "TCU-6001",
                "gin_classification": "1004728",
                "oem_classification": "Connectivity",
                "oem_description": "Vehicle connectivity system"
            },
            {
                "name": "Radar Sensor Front",
                "part_id": "RSF-7001",
                "gin_classification": "1004729",
                "oem_classification": "ADAS Sensor",
                "oem_description": "Forward radar detection system"
            },
            {
                "name": "Camera Module",
                "part_id": "CAM-7002",
                "gin_classification": "1004730",
                "oem_classification": "ADAS Sensor",
                "oem_description": "Vision camera system"
            },
            {
                "name": "Ultrasonic Sensor",
                "part_id": "USS-7003",
                "gin_classification": "1004731",
                "oem_classification": "Parking Sensor",
                "oem_description": "Ultrasonic proximity sensor"
            }
        ]

        self.sub_components = [
            {
                "name": "Microcontroller",
                "part_id": "MCU-8001",
                "gin_classification": "1004732",
                "oem_classification": "Semiconductor",
                "oem_description": "Main processing unit"
            },
            {
                "name": "CAN Transceiver",
                "part_id": "CAN-8002",
                "gin_classification": "1004733",
                "oem_classification": "Communication IC",
                "oem_description": "CAN bus interface chip"
            },
            {
                "name": "Power Supply Module",
                "part_id": "PSM-8003",
                "gin_classification": "1004734",
                "oem_classification": "Power Management",
                "oem_description": "Voltage regulation module"
            },
            {
                "name": "Sensor Interface",
                "part_id": "SIF-8004",
                "gin_classification": "1004735",
                "oem_classification": "Interface Circuit",
                "oem_description": "Sensor signal conditioning"
            },
            {
                "name": "Flash Memory",
                "part_id": "FLM-8005",
                "gin_classification": "1004736",
                "oem_classification": "Memory Device",
                "oem_description": "Non-volatile memory storage"
            },
            {
                "name": "Crystal Oscillator",
                "part_id": "XTL-8006",
                "gin_classification": "1004737",
                "oem_classification": "Timing Device",
                "oem_description": "Clock generation component"
            },
            {
                "name": "Voltage Regulator",
                "part_id": "VRG-8007",
                "gin_classification": "1004738",
                "oem_classification": "Power IC",
                "oem_description": "Voltage regulation circuit"
            },
            {
                "name": "Capacitor Bank",
                "part_id": "CAP-8008",
                "gin_classification": "1004739",
                "oem_classification": "Passive Component",
                "oem_description": "Energy storage components"
            },
            {
                "name": "Connector Assembly",
                "part_id": "CON-8009",
                "gin_classification": "1004740",
                "oem_classification": "Mechanical Interface",
                "oem_description": "Electrical connection system"
            },
            {
                "name": "PCB Assembly",
                "part_id": "PCB-8010",
                "gin_classification": "1004741",
                "oem_classification": "Circuit Board",
                "oem_description": "Printed circuit board assembly"
            }
        ]

        self.countries = ["DEU", "USA", "JPN", "KOR", "CHN", "CZE", "POL", "HUN"]

    def generate_uuid(self) -> str:
        """Generate a unique Catena-X ID"""
        return f"urn:uuid:{uuid.uuid4()}"

    def generate_serial_number(self, prefix: str) -> str:
        """Generate a unique serial number"""
        return f"{prefix}-{random.randint(100000000, 999999999)}"

    def generate_vin(self) -> str:
        """Generate a realistic Vehicle Identification Number (VIN)"""
        # VIN format: WMI(3) + VDS(6) + VIS(8) = 17 characters
        wmi = "WBA"  # BMW example
        vds = f"{random.randint(100000, 999999)}"
        vis = f"{random.randint(10000000, 99999999)}"
        return f"{wmi}{vds}{vis}"

    def generate_manufacturing_date(self, days_offset: int) -> str:
        """Generate manufacturing date with specified days offset from base date"""
        date = self.base_date - timedelta(days=days_offset)
        return date.strftime("%Y-%m-%dT%H:%M:%S")

    def create_serial_part_vehicle(self, catena_x_id: str, bpnl: str, bpns: str, part_info: Dict,
                                 days_offset: int, instance_prefix: str) -> Dict[str, Any]:
        """Create a serial part object for a vehicle (includes VAN)"""
        vin = self.generate_vin()

        return {
            "localIdentifiers": [
                {
                    "key": "manufacturerId",
                    "value": bpnl
                },
                {
                    "key": "manufacturerPartId",
                    "value": part_info["part_id"]
                },
                {
                    "key": "partInstanceId",
                    "value": self.generate_serial_number(instance_prefix)
                },
                {
                    "key": "van",
                    "value": vin
                }
            ],
            "manufacturingInformation": {
                "date": self.generate_manufacturing_date(days_offset),
                "country": random.choice(self.countries),
                "sites": [
                    {
                        "catenaXsiteId": bpns,
                        "function": "production"
                    }
                ]
            },
            "catenaXId": catena_x_id,
            "partTypeInformation": {
                "manufacturerPartId": part_info["part_id"],
                "nameAtManufacturer": part_info["name"],
                "partClassification": [
                    {
                        "classificationStandard": "GIN 20510-21513",
                        "classificationID": part_info["gin_classification"],
                        "classificationDescription": "Generic standard for classification of parts in the automotive industry."
                    },
                    {
                        "classificationStandard": "OEM Part Classification 1022-102",
                        "classificationID": part_info["oem_classification"],
                        "classificationDescription": part_info["oem_description"]
                    }
                ]
            }
        }

    def create_serial_part_component(self, catena_x_id: str, bpnl: str, bpns: str, part_info: Dict,
                                   days_offset: int, instance_prefix: str, customer_part_id: str = None) -> Dict[str, Any]:
        """Create a serial part object for a component (no VAN)"""
        local_identifiers = [
            {
                "key": "manufacturerId",
                "value": bpnl
            },
            {
                "key": "manufacturerPartId",
                "value": part_info["part_id"]
            },
            {
                "key": "partInstanceId",
                "value": self.generate_serial_number(instance_prefix)
            }
        ]

        part_type_info = {
            "manufacturerPartId": part_info["part_id"],
            "nameAtManufacturer": part_info["name"],
            "partClassification": [
                {
                    "classificationStandard": "GIN 20510-21513",
                    "classificationID": part_info["gin_classification"],
                    "classificationDescription": "Generic standard for classification of parts in the automotive industry."
                },
                {
                    "classificationStandard": "OEM Part Classification 1022-102",
                    "classificationID": part_info["oem_classification"],
                    "classificationDescription": part_info["oem_description"]
                }
            ]
        }

        # Add customer part information if provided
        if customer_part_id:
            part_type_info["customerPartId"] = customer_part_id
            part_type_info["nameAtCustomer"] = part_info["name"]

        return {
            "localIdentifiers": local_identifiers,
            "manufacturingInformation": {
                "date": self.generate_manufacturing_date(days_offset),
                "country": random.choice(self.countries),
                "sites": [
                    {
                        "catenaXsiteId": bpns,
                        "function": "production"
                    }
                ]
            },
            "catenaXId": catena_x_id,
            "partTypeInformation": part_type_info
        }

    def create_child_item(self, catena_x_id: str, bpnl: str) -> Dict[str, Any]:
        """Create a child item for BOM relationship"""
        return {
            "catenaXId": catena_x_id,
            "quantity": {
                "quantityNumber": 1.0,
                "measurementUnit": "unit:piece"
            },
            "hasAlternatives": False,
            "businessPartner": bpnl,
            "createdOn": datetime.now().isoformat() + "Z",
            "lastModifiedOn": datetime.now().isoformat() + "Z"
        }

    def create_parent_item(self, catena_x_id: str, bpnl: str) -> Dict[str, Any]:
        """Create a parent item for usage relationship"""
        return {
            "catenaXId": catena_x_id,
            "quantity": {
                "quantityNumber": 1.0,
                "measurementUnit": "unit:piece"
            },
            "businessPartner": bpnl,
            "createdOn": datetime.now().isoformat() + "Z",
            "lastModifiedOn": datetime.now().isoformat() + "Z"
        }

    def generate_vehicle_hierarchy(self, vehicle_number: int) -> List[Dict[str, Any]]:
        """Generate complete vehicle hierarchy with 3 levels"""
        parts = []

        # Level 1: Vehicle (OEM)
        vehicle_id = self.generate_uuid()
        vehicle_info = {
            "name": f"Electric Vehicle Model-{vehicle_number:04d}",
            "part_id": f"EV-MODEL-{vehicle_number:04d}",
            "gin_classification": "1004712",
            "oem_classification": "Electric vehicle",
            "oem_description": "Complete electric vehicle assembly"
        }

        # Level 2 parts: 5 from OEM, 15 from Tier1
        level2_parts = []

        # 5 OEM parts
        oem_level2_parts = random.sample(self.oem_parts, 5)
        for i, part_info in enumerate(oem_level2_parts):
            part_id = self.generate_uuid()
            level2_parts.append({
                "id": part_id,
                "bpnl": self.bpnl_oem,
                "bpns": self.bpns_oem,
                "info": part_info,
                "sub_count": random.randint(1, 2)
            })

        # 15 Tier1 parts
        tier1_level2_parts = random.sample(self.tier1_parts, 15)
        for i, part_info in enumerate(tier1_level2_parts):
            part_id = self.generate_uuid()
            level2_parts.append({
                "id": part_id,
                "bpnl": self.bpnl_tier1,
                "bpns": self.bpns_tier1,
                "info": part_info,
                "sub_count": random.randint(1, 3)
            })

        # Generate Level 3 parts and create all parts
        vehicle_children = []

        for level2_part in level2_parts:
            # Create Level 2 part
            level2_children = []

            # Generate Level 3 subcomponents
            for j in range(level2_part["sub_count"]):
                sub_info = random.choice(self.sub_components)
                sub_id = self.generate_uuid()

                # Level 3 part (always from Tier1)
                sub_part = {
                    "catenaXId": sub_id,
                    "bpnl": self.bpnl_tier1,
                    "urn:samm:io.catenax.serial_part:3.0.0#SerialPart": self.create_serial_part_component(
                        sub_id, self.bpnl_tier1, self.bpns_tier1, sub_info,
                        random.randint(30, 90), "SUB", f"CUST-{sub_info['part_id']}"
                    ),
                    "urn:samm:io.catenax.single_level_usage_as_built:3.0.0#SingleLevelUsageAsBuilt": {
                        "catenaXId": sub_id,
                        "customers": [self.bpnl_tier1],
                        "parentItems": [self.create_parent_item(level2_part["id"], level2_part["bpnl"])]
                    }
                }
                parts.append(sub_part)
                level2_children.append(self.create_child_item(sub_id, self.bpnl_tier1))

            # Level 2 part
            customer_part_id = f"CUST-{level2_part['info']['part_id']}" if level2_part["bpnl"] == self.bpnl_tier1 else None

            level2_twin = {
                "catenaXId": level2_part["id"],
                "bpnl": level2_part["bpnl"],
                "urn:samm:io.catenax.serial_part:3.0.0#SerialPart": self.create_serial_part_component(
                    level2_part["id"], level2_part["bpnl"], level2_part["bpns"],
                    level2_part["info"], random.randint(7, 30), "L2P", customer_part_id
                ),
                "urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt": {
                    "catenaXId": level2_part["id"],
                    "childItems": level2_children
                },
                "urn:samm:io.catenax.single_level_usage_as_built:3.0.0#SingleLevelUsageAsBuilt": {
                    "catenaXId": level2_part["id"],
                    "customers": [level2_part["bpnl"]],
                    "parentItems": [self.create_parent_item(vehicle_id, self.bpnl_oem)]
                }
            }
            parts.append(level2_twin)
            vehicle_children.append(self.create_child_item(level2_part["id"], level2_part["bpnl"]))

        # Level 1: Vehicle (uses vehicle-specific serial part creation)
        vehicle = {
            "catenaXId": vehicle_id,
            "bpnl": self.bpnl_oem,
            "urn:samm:io.catenax.serial_part:3.0.0#SerialPart": self.create_serial_part_vehicle(
                vehicle_id, self.bpnl_oem, self.bpns_oem, vehicle_info, 0, "VEH"
            ),
            "urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt": {
                "catenaXId": vehicle_id,
                "childItems": vehicle_children
            }
        }
        parts.append(vehicle)

        return parts

    def generate_test_data(self, num_vehicles: int) -> Dict[str, Any]:
        """Generate complete test data structure"""
        all_parts = []

        for i in range(1, num_vehicles + 1):
            vehicle_parts = self.generate_vehicle_hierarchy(i)
            all_parts.extend(vehicle_parts)

        return {
            "policies": {
                "traceability-core": {
                    "@context": {
                        "odrl": "http://www.w3.org/ns/odrl/2/",
                        "cx-policy": "https://w3id.org/catenax/policy/"
                    },
                    "@type": "PolicyDefinitionRequestDto",
                    "@id": "traceability-core",
                    "policy": {
                        "@type": "odrl:Set",
                        "odrl:permission": [
                            {
                                "odrl:action": "use",
                                "odrl:constraint": {
                                    "odrl:and": [
                                        {
                                            "odrl:leftOperand": "https://w3id.org/catenax/policy/FrameworkAgreement",
                                            "odrl:operator": {
                                                "@id": "odrl:eq"
                                            },
                                            "odrl:rightOperand": "traceability:1.0"
                                        },
                                        {
                                            "odrl:leftOperand": "https://w3id.org/catenax/policy/UsagePurpose",
                                            "odrl:operator": {
                                                "@id": "odrl:eq"
                                            },
                                            "odrl:rightOperand": "cx.core.industrycore:1"
                                        }
                                    ]
                                }
                            }
                        ]
                    }
                }
            },
            "https://catenax.io/schema/TestDataContainer/1.0.0": all_parts
        }

def main():
    parser = argparse.ArgumentParser(description='Generate CX-0127 compliant automotive test data')
    parser.add_argument('bpnl_oem', help='BPNL for OEM (e.g., BPNL000000002OEM)')
    parser.add_argument('bpnl_tier1', help='BPNL for Tier 1 Supplier (e.g., BPNL000000002T1S)')
    parser.add_argument('num_vehicles', type=int, help='Number of vehicles to generate')
    parser.add_argument('--output', '-o', default='automotive-test-data.json',
                       help='Output filename (default: automotive-test-data.json)')

    args = parser.parse_args()

    generator = AutomotivePartGenerator(args.bpnl_oem, args.bpnl_tier1)
    test_data = generator.generate_test_data(args.num_vehicles)

    with open(args.output, 'w', encoding='utf-8') as f:
        json.dump(test_data, f, indent=2, ensure_ascii=False)

    print(f"Generated CX-0127 compliant test data for {args.num_vehicles} vehicles")
    print(f"Total parts generated: {len(test_data['https://catenax.io/schema/TestDataContainer/1.0.0'])}")
    print(f"Output written to: {args.output}")

if __name__ == "__main__":
    main()
