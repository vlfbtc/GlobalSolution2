-- EXECUTAR COMO USUÁRIO ADMIN (por exemplo, SYSTEM) NA CRIAÇÃO INICIAL DO BANCO

-- 1) Usuário / schema da aplicação
CREATE USER VAGAS_CURSOS IDENTIFIED BY 123456
  DEFAULT TABLESPACE users
  TEMPORARY TABLESPACE temp
  QUOTA UNLIMITED ON users;

GRANT CONNECT, RESOURCE TO VAGAS_CURSOS;

ALTER SESSION SET CURRENT_SCHEMA = VAGAS_CURSOS;

-------------------------------------------------
-- 2) Tabelas principais
-------------------------------------------------

-- EMPRESA
CREATE TABLE Empresa (
  empresa_id  NUMBER        PRIMARY KEY,
  nome        VARCHAR2(100) NOT NULL,
  email       VARCHAR2(100) UNIQUE NOT NULL,
  senha       VARCHAR2(100) NOT NULL
);

CREATE SEQUENCE seq_empresa START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_empresa
BEFORE INSERT ON Empresa
FOR EACH ROW
BEGIN
  :NEW.empresa_id := seq_empresa.NEXTVAL;
END;
/

-- CANDIDATO
CREATE TABLE Candidato (
  candidato_id NUMBER        PRIMARY KEY,
  nome         VARCHAR2(100) NOT NULL,
  email        VARCHAR2(100) UNIQUE NOT NULL,
  senha        VARCHAR2(100) NOT NULL,
  curriculo    CLOB
);

CREATE SEQUENCE seq_candidato START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_candidato
BEFORE INSERT ON Candidato
FOR EACH ROW
BEGIN
  :NEW.candidato_id := seq_candidato.NEXTVAL;
END;
/

-- VAGA
CREATE TABLE Vaga (
  vaga_id    NUMBER        PRIMARY KEY,
  titulo     VARCHAR2(100) NOT NULL,
  descricao  CLOB,
  empresa_id NUMBER        NOT NULL,
  CONSTRAINT fk_vaga_empresa
    FOREIGN KEY (empresa_id) REFERENCES Empresa(empresa_id)
);

CREATE SEQUENCE seq_vaga START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_vaga
BEFORE INSERT ON Vaga
FOR EACH ROW
BEGIN
  :NEW.vaga_id := seq_vaga.NEXTVAL;
END;
/

-- CANDIDATURA (liga Candidato x Vaga)
CREATE TABLE Candidatura (
  candidatura_id NUMBER       PRIMARY KEY,
  candidato_id   NUMBER       NOT NULL,
  vaga_id        NUMBER       NOT NULL,
  status         VARCHAR2(50) DEFAULT 'Pendente',
  data_aplicacao DATE         DEFAULT SYSDATE,
  CONSTRAINT fk_cand_candidato
    FOREIGN KEY (candidato_id) REFERENCES Candidato(candidato_id),
  CONSTRAINT fk_cand_vaga
    FOREIGN KEY (vaga_id) REFERENCES Vaga(vaga_id)
);

CREATE SEQUENCE seq_candidatura START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_candidatura
BEFORE INSERT ON Candidatura
FOR EACH ROW
BEGIN
  :NEW.candidatura_id := seq_candidatura.NEXTVAL;
END;
/

-- CURSO
CREATE TABLE Curso (
  curso_id  NUMBER        PRIMARY KEY,
  titulo    VARCHAR2(100) NOT NULL,
  categoria VARCHAR2(100)
);

CREATE SEQUENCE seq_curso START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_curso
BEFORE INSERT ON Curso
FOR EACH ROW
BEGIN
  :NEW.curso_id := seq_curso.NEXTVAL;
END;
/

-- CURSO x CANDIDATO (progresso do curso)
CREATE TABLE CursoCandidato (
  id           NUMBER        PRIMARY KEY,
  candidato_id NUMBER        NOT NULL,
  curso_id     NUMBER        NOT NULL,
  progresso    NUMBER(3)     DEFAULT 0, -- 0 a 100
  CONSTRAINT fk_cc_candidato
    FOREIGN KEY (candidato_id) REFERENCES Candidato(candidato_id),
  CONSTRAINT fk_cc_curso
    FOREIGN KEY (curso_id)     REFERENCES Curso(curso_id),
  CONSTRAINT uc_cc UNIQUE (candidato_id, curso_id)
);

CREATE SEQUENCE seq_curso_cand START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_curso_cand
BEFORE INSERT ON CursoCandidato
FOR EACH ROW
BEGIN
  :NEW.id := seq_curso_cand.NEXTVAL;
END;
/

-- VAGA x CURSO (cursos recomendados para a vaga)
CREATE TABLE VagaCurso (
  id       NUMBER PRIMARY KEY,
  vaga_id  NUMBER NOT NULL,
  curso_id NUMBER NOT NULL,
  CONSTRAINT fk_vc_vaga
    FOREIGN KEY (vaga_id)  REFERENCES Vaga(vaga_id),
  CONSTRAINT fk_vc_curso
    FOREIGN KEY (curso_id) REFERENCES Curso(curso_id),
  CONSTRAINT uc_vc UNIQUE (vaga_id, curso_id)
);

CREATE SEQUENCE seq_vaga_curso START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_vaga_curso
BEFORE INSERT ON VagaCurso
FOR EACH ROW
BEGIN
  :NEW.id := seq_vaga_curso.NEXTVAL;
END;
/

-------------------------------------------------
-- 3) Tabelas de LOG e métricas
-------------------------------------------------

CREATE TABLE Activity_Log (
  log_id       NUMBER        PRIMARY KEY,
  usuario_tipo VARCHAR2(20), -- 'CANDIDATO' ou 'EMPRESA'
  usuario_id   NUMBER,
  acao         VARCHAR2(50),
  detalhes     VARCHAR2(200),
  data_hora    TIMESTAMP DEFAULT SYSTIMESTAMP
);

CREATE SEQUENCE seq_activity_log START WITH 1 INCREMENT BY 1;

CREATE OR REPLACE TRIGGER bi_activity_log
BEFORE INSERT ON Activity_Log
FOR EACH ROW
BEGIN
  :NEW.log_id := seq_activity_log.NEXTVAL;
END;
/

CREATE TABLE CursoMetricas (
  curso_id              NUMBER      PRIMARY KEY,
  vagas_requerendo      NUMBER,
  total_vagas           NUMBER,
  perc_vagas_requerendo NUMBER(5,2)
);

-------------------------------------------------
-- 4) TRIGGERS de auditoria/uso
-------------------------------------------------

-- Log quando uma nova candidatura é criada
CREATE OR REPLACE TRIGGER trg_log_candidatura
AFTER INSERT ON Candidatura
FOR EACH ROW
DECLARE
  v_cand_id NUMBER := :NEW.candidato_id;
  v_vaga_id NUMBER := :NEW.vaga_id;
BEGIN
  INSERT INTO Activity_Log (usuario_tipo, usuario_id, acao, detalhes)
  VALUES ('CANDIDATO', v_cand_id, 'APLICACAO_VAGA',
          'Candidato ' || v_cand_id || ' aplicou-se à vaga ' || v_vaga_id);
END;
/

-- Log quando um curso é concluído (progresso de <100 para 100)
CREATE OR REPLACE TRIGGER trg_log_conclusao_curso
AFTER UPDATE OF progresso ON CursoCandidato
FOR EACH ROW
WHEN (NEW.progresso = 100 AND OLD.progresso < 100)
DECLARE
  v_cand_id  NUMBER := :NEW.candidato_id;
  v_curso_id NUMBER := :NEW.curso_id;
BEGIN
  INSERT INTO Activity_Log (usuario_tipo, usuario_id, acao, detalhes)
  VALUES ('CANDIDATO', v_cand_id, 'CONCLUIR_CURSO',
          'Candidato ' || v_cand_id || ' concluiu curso ' || v_curso_id);
END;
/

-- Log quando uma nova vaga é criada
CREATE OR REPLACE TRIGGER trg_log_vaga
AFTER INSERT ON Vaga
FOR EACH ROW
DECLARE
  v_emp_id  NUMBER := :NEW.empresa_id;
  v_vaga_id NUMBER := :NEW.vaga_id;
BEGIN
  INSERT INTO Activity_Log (usuario_tipo, usuario_id, acao, detalhes)
  VALUES ('EMPRESA', v_emp_id, 'CRIAR_VAGA',
          'Empresa ' || v_emp_id || ' criou vaga ' || v_vaga_id);
END;
/

-------------------------------------------------
-- 5) PROCEDURE para métricas de cursos
-------------------------------------------------

CREATE OR REPLACE PROCEDURE proc_atualiza_metrica_cursos IS
  v_total_vagas NUMBER;
BEGIN
  SELECT COUNT(*) INTO v_total_vagas FROM Vaga;

  FOR rec IN (
    SELECT c.curso_id,
           (SELECT COUNT(*) FROM VagaCurso vc
             WHERE vc.curso_id = c.curso_id) AS vagas_req
    FROM Curso c
  )
  LOOP
    MERGE INTO CursoMetricas m
    USING DUAL
    ON (m.curso_id = rec.curso_id)
    WHEN MATCHED THEN
      UPDATE SET
        m.vagas_requerendo      = rec.vagas_req,
        m.total_vagas           = v_total_vagas,
        m.perc_vagas_requerendo =
          CASE WHEN v_total_vagas > 0
               THEN (rec.vagas_req * 100.0 / v_total_vagas)
               ELSE 0
          END
    WHEN NOT MATCHED THEN
      INSERT (curso_id, vagas_requerendo, total_vagas, perc_vagas_requerendo)
      VALUES (
        rec.curso_id,
        rec.vagas_req,
        v_total_vagas,
        CASE WHEN v_total_vagas > 0
             THEN (rec.vagas_req * 100.0 / v_total_vagas)
             ELSE 0
        END
      );
  END LOOP;
END;
/
