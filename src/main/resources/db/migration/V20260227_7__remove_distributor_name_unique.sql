DO $$
DECLARE
    c RECORD;
BEGIN
    FOR c IN
        SELECT con.conname
        FROM pg_constraint con
                 JOIN pg_class rel ON rel.oid = con.conrelid
                 JOIN pg_namespace nsp ON nsp.oid = rel.relnamespace
                 JOIN pg_attribute att ON att.attrelid = rel.oid AND att.attnum = ANY (con.conkey)
        WHERE con.contype = 'u'
          AND rel.relname = 'distributor'
          AND nsp.nspname = 'public'
        GROUP BY con.conname
        HAVING COUNT(*) = 1
           AND MIN(att.attname) = 'distributor_name'
           AND MAX(att.attname) = 'distributor_name'
    LOOP
        EXECUTE format('ALTER TABLE public.distributor DROP CONSTRAINT IF EXISTS %I', c.conname);
    END LOOP;
END $$;
