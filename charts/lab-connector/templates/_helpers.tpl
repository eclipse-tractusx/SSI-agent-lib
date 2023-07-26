{{/*
Expand the name of the chart.
*/}}
{{- define "connector.name" -}}
{{- default .Chart.Name .Values.nameOverride | replace "+" "_"  | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Create a default fully qualified app name.
We truncate at 63 chars because some Kubernetes name fields are limited to this (by the DNS naming spec).
If release name contains chart name it will be used as a full name.
*/}}
{{- define "connector.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
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
{{- define "connector.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Control Common labels
*/}}
{{- define "connector.labels" -}}
helm.sh/chart: {{ include "connector.chart" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Control Common labels
*/}}
{{- define "connector.controlplane.labels" -}}
helm.sh/chart: {{ include "connector.chart" . }}
{{ include "connector.controlplane.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/component: edc-controlplane
app.kubernetes.io/part-of: edc
{{- end }}

{{/*
Data Common labels
*/}}
{{- define "connector.dataplane.labels" -}}
helm.sh/chart: {{ include "connector.chart" . }}
{{ include "connector.dataplane.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/component: edc-dataplane
app.kubernetes.io/part-of: edc
{{- end }}

{{/*
Control Selector labels
*/}}
{{- define "connector.controlplane.selectorLabels" -}}
app.kubernetes.io/name: {{ include "connector.name" . }}-controlplane
app.kubernetes.io/instance: {{ .Release.Name }}-controlplane
{{- end }}

{{/*
Data Selector labels
*/}}
{{- define "connector.dataplane.selectorLabels" -}}
app.kubernetes.io/name: {{ include "connector.name" . }}-dataplane
app.kubernetes.io/instance: {{ .Release.Name }}-dataplane
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "connector.controlplane.serviceaccount.name" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "connector.fullname" . ) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "connector.dataplane.serviceaccount.name" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "connector.fullname" . ) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

{{/*
Control IDS URL
*/}}
{{- define "connector.controlplane.url.ids" -}}
{{- if .Values.controlplane.url.ids }}{{/* if ids api url has been specified explicitly */}}
{{- .Values.controlplane.url.ids }}
{{- else }}{{/* else when ids api url has not been specified explicitly */}}
{{- with (index .Values.controlplane.ingresses 0) }}
{{- if .enabled }}{{/* if ingress enabled */}}
{{- if .tls.enabled }}{{/* if TLS enabled */}}
{{- printf "https://%s" .hostname -}}
{{- else }}{{/* else when TLS not enabled */}}
{{- printf "http://%s" .hostname -}}
{{- end }}{{/* end if tls */}}
{{- else }}{{/* else when ingress not enabled */}}
{{- printf "http://%s-controlplane:%v" ( include "connector.fullname" $ ) $.Values.controlplane.endpoints.ids.port -}}
{{- end }}{{/* end if ingress */}}
{{- end }}{{/* end with ingress */}}
{{- end }}{{/* end if .Values.controlplane.url.ids */}}
{{- end }}


{{/*
Control SSI URL
*/}}
{{- define "connector.did.document.url" -}}
{{- with (index .Values.controlplane.ingresses 0) }}
{{- if .enabled }}{{/* if ingress enabled */}}
{{- if .tls.enabled }}{{/* if TLS enabled */}}
{{- printf "https://%s" .hostname -}}
{{- else }}{{/* else when TLS not enabled */}}
{{- printf "http://%s" .hostname -}}
{{- end }}{{/* end if tls */}}
{{- else }}{{/* else when ingress not enabled */}}
{{- printf "http://%s-controlplane:%v" ( include "connector.fullname" $ ) $.Values.controlplane.endpoints.wellknown.port -}}
{{- end }}{{/* end if ingress */}}
{{- end }}{{/* end with ingress */}}
{{- end }}


{{/*
Control SSI URL
*/}}
{{- define "connector.did.document.host" -}}
{{- with (index .Values.controlplane.ingresses 0) }}
{{- if .enabled }}{{/* if ingress enabled */}}
{{- printf "%s" .hostname -}}
{{- else }}{{/* else when ingress not enabled */}}
{{- printf "%s-controlplane:%v%v" ( include "connector.fullname" $ ) $.Values.controlplane.endpoints.wellknown.port $.Values.controlplane.endpoints.wellknown.path  -}}
{{- end }}{{/* end if ingress */}}
{{- end }}
{{- end }}


{{/*
Control IDS URL
*/}}
{{- define "connector.controlplane.url.validation" -}}
{{- printf "http://%s-controlplane:%v%s/token" ( include "connector.fullname" $ ) $.Values.controlplane.endpoints.validation.port $.Values.controlplane.endpoints.validation.path -}}
{{- end }}

{{/*
Data Control URL
*/}}
{{- define "connector.dataplane.url.control" -}}
{{- printf "http://%s-dataplane:%v%s" (include "connector.fullname" . ) .Values.dataplane.endpoints.control.port .Values.dataplane.endpoints.control.path -}}
{{- end }}

{{/*
Data Public URL
*/}}
{{- define "connector.dataplane.url.public" -}}
{{- if .Values.dataplane.url.public }}{{/* if public api url has been specified explicitly */}}
{{- .Values.dataplane.url.public }}
{{- else }}{{/* else when public api url has not been specified explicitly */}}
{{- with (index  .Values.dataplane.ingresses 0) }}
{{- if .enabled }}{{/* if ingress enabled */}}
{{- if .tls.enabled }}{{/* if TLS enabled */}}
{{- printf "https://%s%s" .hostname $.Values.dataplane.endpoints.public.path -}}
{{- else }}{{/* else when TLS not enabled */}}
{{- printf "http://%s%s" .hostname $.Values.dataplane.endpoints.public.path -}}
{{- end }}{{/* end if tls */}}
{{- else }}{{/* else when ingress not enabled */}}
{{- printf "http://%s-dataplane:%v%s" (include "connector.fullname" $ ) $.Values.dataplane.endpoints.public.port $.Values.dataplane.endpoints.public.path -}}
{{- end }}{{/* end if ingress */}}
{{- end }}{{/* end with ingress */}}
{{- end }}{{/* end if .Values.dataplane.url.public */}}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "connector.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "connector.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}
