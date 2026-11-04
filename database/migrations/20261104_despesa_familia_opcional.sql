-- Executar no schema remoto antes de iniciar o backend com o mapeamento corrigido.
-- Preserva os dados e as colunas legadas; nao executa automaticamente no startup.
BEGIN;
SET LOCAL lock_timeout = '5s';
ALTER TABLE public.despesas ALTER COLUMN fk_familia DROP NOT NULL;
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
                   WHERE conrelid = 'public.usuario'::regclass
                     AND conname = 'uq_usuario_id_despesas') THEN
        ALTER TABLE public.usuario ADD CONSTRAINT uq_usuario_id_despesas UNIQUE (id);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint
                   WHERE conrelid = 'public.despesas'::regclass
                     AND conname = 'fk_despesas_responsavel') THEN
        ALTER TABLE public.despesas ADD CONSTRAINT fk_despesas_responsavel
            FOREIGN KEY (fk_usuario) REFERENCES public.usuario (id);
    END IF;
END $$;
-- A FK composta existente continua validando a familia quando informada.
-- A FK independente garante um responsavel valido mesmo sem familia.
COMMIT;
