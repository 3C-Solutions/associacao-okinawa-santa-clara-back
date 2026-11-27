BEGIN;
SET LOCAL lock_timeout = '5s';

ALTER TABLE public.despesas
    ADD COLUMN IF NOT EXISTS fk_reembolso INTEGER;

DO $$
DECLARE
    legado_tem_registros BOOLEAN;
    constraint_legada RECORD;
BEGIN
    IF to_regclass('public.solicitacao_reembolso') IS NOT NULL THEN
        EXECUTE 'SELECT EXISTS (SELECT 1 FROM public.solicitacao_reembolso)'
            INTO legado_tem_registros;
        IF legado_tem_registros THEN
            RAISE EXCEPTION
                'solicitacao_reembolso ainda contém dados; migre-os antes de habilitar o fluxo em lancamento';
        END IF;

        FOR constraint_legada IN
            SELECT conname
            FROM pg_constraint
            WHERE contype = 'f'
              AND conrelid = 'public.despesas'::regclass
              AND confrelid = 'public.solicitacao_reembolso'::regclass
        LOOP
            EXECUTE format(
                'ALTER TABLE public.despesas DROP CONSTRAINT %I',
                constraint_legada.conname);
        END LOOP;
    END IF;

    IF EXISTS (
        SELECT 1
        FROM public.despesas d
        WHERE d.fk_reembolso IS NOT NULL
          AND NOT EXISTS (
              SELECT 1
              FROM public.lancamento l
              WHERE l.id = d.fk_reembolso
                AND l.tipo_lancamento = 'REEMBOLSO'
          )
    ) THEN
        RAISE EXCEPTION
            'despesas contém vínculos de reembolso sem lançamento REEMBOLSO correspondente';
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE contype = 'f'
          AND conrelid = 'public.despesas'::regclass
          AND confrelid = 'public.lancamento'::regclass
          AND conkey = ARRAY[
              (SELECT attnum FROM pg_attribute
               WHERE attrelid = 'public.despesas'::regclass AND attname = 'fk_reembolso')
          ]::smallint[]
    ) THEN
        ALTER TABLE public.despesas
            ADD CONSTRAINT fk_despesas_lancamento_reembolso
            FOREIGN KEY (fk_reembolso) REFERENCES public.lancamento(id);
    END IF;
END $$;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE contype = 'u'
          AND conrelid = 'public.despesas'::regclass
          AND conname = 'uq_despesas_fk_reembolso'
    ) THEN
        ALTER TABLE public.despesas
            ADD CONSTRAINT uq_despesas_fk_reembolso UNIQUE (fk_reembolso);
    END IF;
END $$;

COMMIT;
