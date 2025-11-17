ALTER SESSION SET CURRENT_SCHEMA = VAGAS_CURSOS;

-- Cursos
INSERT INTO Curso (titulo, categoria) VALUES ('Introdução a Kotlin', 'Programação');
INSERT INTO Curso (titulo, categoria) VALUES ('Banco de Dados Oracle Básico', 'Banco de Dados');
INSERT INTO Curso (titulo, categoria) VALUES ('Lógica de Programação', 'Fundamentos');

-- Empresa (id será 1, pois a sequence inicia em 1)
INSERT INTO Empresa (nome, email, senha)
VALUES ('Tech Soluções', 'empresa@tech.com', '123456');

-- Candidato (id será 1)
INSERT INTO Candidato (nome, email, senha, curriculo)
VALUES ('João da Silva', 'joao@teste.com', '123456', 'Desenvolvedor Jr. em busca de oportunidade.');

-- Vaga (id será 1)
INSERT INTO Vaga (titulo, descricao, empresa_id)
VALUES ('Desenvolvedor Kotlin Jr',
        'Vaga para dev jr com interesse em Kotlin e banco de dados Oracle.',
        1);

-- Vaga 2 (id será 2)
INSERT INTO Vaga (titulo, descricao, empresa_id)
VALUES ('Estágio em Suporte Técnico',
        'Suporte ao usuário e triagem de chamados.',
        1);

-- Associação vaga x curso (recomendações)
-- Vaga 1 pede cursos 1 e 2
INSERT INTO VagaCurso (vaga_id, curso_id) VALUES (1, 1);
INSERT INTO VagaCurso (vaga_id, curso_id) VALUES (1, 2);

-- Vaga 2 pede apenas curso de Lógica
INSERT INTO VagaCurso (vaga_id, curso_id) VALUES (2, 3);
