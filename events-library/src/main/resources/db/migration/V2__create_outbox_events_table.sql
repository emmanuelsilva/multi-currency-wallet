CREATE TABLE IF NOT EXISTS outbox_events (
    id              UUID DEFAULT public.uuid_generate_v4() PRIMARY KEY,
    idempotent_key  VARCHAR(255) NOT NULL,
    topic           VARCHAR(255) NOT NULL,
    key             VARCHAR(255) NULL,
    event           VARCHAR(255) NOT NULL,
    payload         JSONB        NOT NULL,
    status          VARCHAR(255) NOT NULL,
    retry_count     SMALLINT     NOT NULL,
    published_at    TIMESTAMP(0) NULL,
    failed_at       TIMESTAMP(0) NULL,
    failed_reason   VARCHAR(255) NULL,
    created_at      TIMESTAMP(0) NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP(0) NOT NULL DEFAULT NOW()
);