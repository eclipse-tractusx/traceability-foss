#    Copyright (c) 2023 Contributors to the Eclipse Foundation
#
#    See the NOTICE file(s) distributed with this work for additional
#    information regarding copyright ownership.
#
#    This program and the accompanying materials are made available under the
#    terms of the Apache License, Version 2.0 which is available at
#    https://www.apache.org/licenses/LICENSE-2.0.
#
#    Unless required by applicable law or agreed to in writing, software
#    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
#    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
#    License for the specific language governing permissions and limitations
#    under the License.
#
#  SPDX-License-Identifier: Apache-2.0

DELETE_DEV_TEST_RESOURCES=(
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-test-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-test&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-test-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-test&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-test&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-test-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-test-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tracex-test-edc-provider-edc-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_TEST_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tracex-test-edc-provider-edc-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_TEST_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-test-edc-provider-tractusx-connector-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-test-edc-provider-tractusx-connector-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_REGISTRY/resource?force=false&orphan=false&resourceName=data-tracex-dt-registry-test-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_TEST_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tracex-dt-registry-test-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_TEST_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-test&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_TEST_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-test-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"

              "$ARGO_DEV_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-dev-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-dev&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-dev-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-dev&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-dev&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-dev-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-dev-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tracex-edc-provider-edc-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_DEV_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tracex-edc-provider-edc-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_DEV_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-tractusx-connector-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-tractusx-connector-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_REGISTRY/resource?force=false&orphan=false&resourceName=data-tracex-dt-registry-dev-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_DEV_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tracex-dt-registry-dev-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_DEV_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-dev&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_DEV_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-dev-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"

              )

DELETE_E2E_RESOURCES=(
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-e2e-a-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-e2e-a&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-e2e-a-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-e2e-a&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-e2e-a&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-e2e-a-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-e2e-a-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tracex-edc-provider-e2e-a-edc-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_A_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tracex-edc-provider-e2e-a-edc-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_A_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-e2e-a-tractusx-connector-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-e2e-a-tractusx-connector-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_REGISTRY/resource?force=false&orphan=false&resourceName=data-tracex-dt-registry-e2e-a-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_A_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tracex-dt-registry-e2e-a-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_A_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-e2e-a&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_A_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-e2e-a-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"


              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-e2e-b-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-e2e-b&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-e2e-b-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-e2e-b&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-e2e-b&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-e2e-b-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-e2e-b-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tracex-edc-provider-e2e-b-edc-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_B_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tracex-edc-provider-e2e-b-edc-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_B_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-e2e-b-tractusx-connector-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tracex-edc-provider-e2e-b-tractusx-connector-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_REGISTRY/resource?force=false&orphan=false&resourceName=data-tracex-dt-registry-e2e-b-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_E2E_B_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tracex-dt-registry-e2e-b-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_E2E_B_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-e2e-b&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_E2E_B_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-e2e-b-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"

            )
DELETE_INT_RESOURCES=(
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-int-a-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-int-a&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-int-a-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-int-a&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-int-a&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-int-a-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-int-a-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tx-edc-provider-postgresql-int-a-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_A_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-provider-postgresql-int-a&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_A_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-provider-int-a-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-provider-int-a-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_REGISTRY/resource?force=false&orphan=false&resourceName=data-tx-registry-int-a-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_A_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tx-registry-int-a-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_A_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-int-a&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_A_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-int-a-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"

              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-backend-postgresql-int-b-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-backend-postgresql-int-b&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?force=false&orphan=false&resourceName=data-tx-edc-consumer-postgresql-int-b-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-postgresql-int-b&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-backend-int-b&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-consumer-int-b-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_TRACE_X_INSTANCE/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-consumer-int-b-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_EDC_PROVIDER/resource?force=false&orphan=false&resourceName=data-tx-edc-provider-postgresql-int-b-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_B_EDC_PROVIDER/resource?group=apps&force=true&orphan=false&resourceName=tx-edc-provider-postgresql-int-b&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_B_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-provider-int-b-tractusx-connector-controlplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_EDC_PROVIDER/resource?group=apps&force=false&orphan=false&resourceName=tx-edc-provider-int-b-tractusx-connector-dataplane&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_REGISTRY/resource?force=false&orphan=false&resourceName=data-tx-registry-int-b-postgresql-0&version=v1&kind=PersistentVolumeClaim&namespace=product-traceability-foss"
              "$ARGO_INT_B_REGISTRY/resource?group=apps&force=true&orphan=false&resourceName=tx-registry-int-b-postgresql&version=v1&kind=StatefulSet&namespace=product-traceability-foss"
              "$ARGO_INT_B_REGISTRY/resource?group=apps&force=false&orphan=false&resourceName=tracex-registry-int-b&version=v1&kind=Deployment&namespace=product-traceability-foss"
              "$ARGO_INT_B_SUBMODELSERVER/resource?group=apps&force=false&orphan=false&resourceName=tracex-int-b-submodelserver&version=v1&kind=Deployment&namespace=product-traceability-foss"

            )
SYNC_DEV_TEST_RESOURCES=(
              "$ARGO_TEST_REGISTRY/sync"
              "$ARGO_TEST_EDC_PROVIDER/sync"
              "$ARGO_TEST_SUBMODELSERVER/sync"
              "$ARGO_TEST_TRACE_X_INSTANCE/sync"

              "$ARGO_DEV_REGISTRY/sync"
              "$ARGO_DEV_EDC_PROVIDER/sync"
              "$ARGO_DEV_SUBMODELSERVER/sync"
              "$ARGO_DEV_TRACE_X_INSTANCE/sync"
              )
SYNC_E2E_RESOURCES=(
              "$ARGO_E2E_A_REGISTRY/sync"
              "$ARGO_E2E_A_EDC_PROVIDER/sync"
              "$ARGO_E2E_A_TRACE_X_INSTANCE/sync"
              "$ARGO_E2E_A_SUBMODELSERVER/sync"

              "$ARGO_E2E_B_REGISTRY/sync"
              "$ARGO_E2E_B_EDC_PROVIDER/sync"
              "$ARGO_E2E_B_SUBMODELSERVER/sync"
              "$ARGO_E2E_B_TRACE_X_INSTANCE/sync"
)
SYNC_INT_RESOURCES=(
              "$ARGO_INT_A_REGISTRY/sync"
              "$ARGO_INT_A_EDC_PROVIDER/sync"
              "$ARGO_INT_A_SUBMODELSERVER/sync"
              "$ARGO_INT_A_TRACE_X_INSTANCE/sync"

              "$ARGO_INT_B_REGISTRY/sync"
              "$ARGO_INT_B_EDC_PROVIDER/sync"
              "$ARGO_INT_B_SUBMODELSERVER/sync"
              "$ARGO_INT_B_TRACE_X_INSTANCE/sync"
)
DEV_TEST_RESOURCES=(
              "$ARGO_TEST_REGISTRY"
              "$ARGO_TEST_EDC_PROVIDER"
              "$ARGO_TEST_TRACE_X_INSTANCE"
              "$ARGO_TEST_SUBMODELSERVER"

              "$ARGO_DEV_REGISTRY"
              "$ARGO_DEV_EDC_PROVIDER"
              "$ARGO_DEV_TRACE_X_INSTANCE"
              "$ARGO_DEV_SUBMODELSERVER"
              )
E2E_RESOURCES=(
              "$ARGO_E2E_A_REGISTRY"
              "$ARGO_E2E_A_EDC_PROVIDER"
              "$ARGO_E2E_A_TRACE_X_INSTANCE"
              "$ARGO_E2E_A_SUBMODELSERVER"

              "$ARGO_E2E_B_REGISTRY"
              "$ARGO_E2E_B_EDC_PROVIDER"
              "$ARGO_E2E_B_TRACE_X_INSTANCE"
              "$ARGO_E2E_B_SUBMODELSERVER"
)
INT_RESOURCES=(
              "$ARGO_INT_A_REGISTRY"
              "$ARGO_INT_A_EDC_PROVIDER"
              "$ARGO_INT_A_TRACE_X_INSTANCE"
              "$ARGO_INT_A_SUBMODELSERVER"

              "$ARGO_INT_B_REGISTRY"
              "$ARGO_INT_B_EDC_PROVIDER"
              "$ARGO_INT_B_TRACE_X_INSTANCE"
              "$ARGO_INT_B_SUBMODELSERVER"
)


