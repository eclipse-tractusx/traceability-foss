# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

_**For better traceability add the corresponding JIRA issue number in each changelog entry, please.**_

## Unreleased

### Changed

- #50 added the discovery type configurable, with a default value of bpnl under Traceability (DISCOVERY_TYPE).
- #55 renamed `backend.edc.apiKey` to `backend.edc.consumerApiKey`
- #99 added configurable publish asset job

### Added

- #55 added configuration of provider EDC API key independent of consumer EDC. I can be configured with `backend.edc.providerApiKey`
- Add separate changelog for cfx. (TRX-347).
