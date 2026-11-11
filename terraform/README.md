# terraform

AWS infrastructure for short-lived demo deployments - **Phase 6**.

Guardrails:
- Create an **AWS Budget alert before the first `terraform apply`**.
- Deploy for a demo, then `terraform destroy` the same day.
- No managed Kafka (MSK) or other always-on services without an explicit decision (ADR).
