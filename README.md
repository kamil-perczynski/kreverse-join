# KReverse Join App

An app to efficiently join two _KTables_ of customers and addresses
so that an update from either side causes the pipeline to run.

```plain
Kafka Topics
  ┌────────────┐
  │ customers  │──┐
  └────────────┘  │       ┌────────────┐       ┌────────────┐
                  ├──────▶│entity_changed│────▶│entity_publish│──┐
  ┌───────────────────┐   └────────────┘       └────────────┘    │
  │ addresses         │──┘                                       ▼
  └───────────────────┘                                   (debounces publications)
                                                         → enriched_invoices
```
