{{/*
Expand the name of the chart.
*/}}
{{- define "traceability-foss-backend.name" -}}
{{- default .Chart.Name .Values.global.backend.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "traceability-foss-backend.fullname" -}}
{{- if .Values.global.backend.fullnameOverride }}
{{- .Values.global.backend.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.global.backend.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "traceability-foss-backend.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "traceability-foss-backend.labels" -}}
helm.sh/chart: {{ include "traceability-foss-backend.chart" . }}
{{ include "traceability-foss-backend.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "traceability-foss-backend.selectorLabels" -}}
app.kubernetes.io/name: {{ include "traceability-foss-backend.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "traceability-foss-backend.serviceAccountName" -}}
{{- if .Values.global.backend.serviceAccount.create }}
{{- default (include "traceability-foss-backend.fullname" .) .Values.global.backend.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.global.backend.serviceAccount.name }}
{{- end }}
{{- end }}
