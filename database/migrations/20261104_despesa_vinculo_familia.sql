-- A familia da despesa e independente da familia do usuario responsavel.
-- Executar apos 20261104_despesa_familia_opcional.sql.
BEGIN;
SET LOCAL lock_timeout = '5s';
ALTER TABLE public.despesas DROP CONSTRAINT IF EXISTS "fkDespesasUsuario";
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
                   WHERE conrelid='public.despesas'::regclass
                     AND conname='fk_despesas_familia') THEN
        ALTER TABLE public.despesas ADD CONSTRAINT fk_despesas_familia
            FOREIGN KEY (fk_familia) REFERENCES public.familia(id);
    END IF;
END $$;
COMMIT;
