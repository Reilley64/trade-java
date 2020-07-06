CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE INDEX CONCURRENTLY IF NOT EXISTS search_index ON assets USING gin((symbol || ' ' || name) gin_trgm_ops);
