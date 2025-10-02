# AuraSage Document Service

Reactive microservice for document management with pluggable persistence and storage adapters.

## Features

- Document metadata management (upload, retrieve, delete)
- Presigned URL generation for direct file uploads/downloads
- Integration with storage service for file operations
- Pluggable persistence adapters (MongoDB, relational databases, DynamoDB)
- Pluggable storage adapters (MinIO, AWS S3, and others)
- OpenAPI/Swagger documentation
- Security with OAuth2 resource server support

## API Endpoints

- `POST /documents/init-upload` - Initialize document upload and get presigned URL
- `GET /documents` - List user's documents
- `GET /documents/{id}` - Get document details
- `GET /documents/{id}/download` - Get download URL for document
- `DELETE /documents/{id}` - Delete document and associated file

## Dependencies

- Persistence adapter (document metadata storage)
- Storage service (file upload/download operations)
- Authentication service

## Running

```bash
./gradlew bootRun
```

Access Swagger UI at: `http://localhost:8080/swagger-ui.html`

## Environment Variables

- `AURASAGE_STORAGE_SERVICE_URL` - Storage service URL (default: http://localhost:8081)
- `TRACING_SAMPLING_PROBABILITY` - Distributed tracing sampling rate (default: 1.0)