CREATE TABLE IF NOT EXISTS customers (
    id         UUID DEFAULT public.uuid_generate_v4() PRIMARY KEY,
    full_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(0) NOT NULL
);