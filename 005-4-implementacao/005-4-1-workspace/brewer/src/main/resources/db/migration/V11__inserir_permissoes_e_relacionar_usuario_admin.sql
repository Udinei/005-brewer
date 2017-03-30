INSERT INTO brewer.permissao (codigo, nome) VALUES (1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO brewer.permissao (codigo, nome) VALUES (2, 'ROLE_CADASTRAR_USUARIO');

INSERT INTO brewer.grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1,1);
INSERT INTO brewer.grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1,2);

INSERT INTO brewer.usuario_grupo (codigo_usuario, codigo_grupo) VALUES (
    (SELECT codigo FROM brewer.usuario WHERE email = 'admin@brewer.com'), 1); 
