from flask import Flask, jsonify, request
from flask_cors import CORS
from flasgger import Swagger
import sqlite3

app = Flask(__name__)
CORS(app)
swagger_config = {
    "headers": [],
    "specs": [
        {
            "endpoint": "apispec",
            "route": "/apispec.json",
            "rule_filter": lambda rule: True,
            "model_filter": lambda tag: True,
        }
    ],
    "static_url_path": "/flasgger_static",
    "swagger_ui": True,
    "specs_route": "/apidocs/"
}
Swagger(app, config=swagger_config)

DB_PATH = "atletas.db"

def init_db():
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute("""
        CREATE TABLE IF NOT EXISTS atletas (
            id        INTEGER PRIMARY KEY AUTOINCREMENT,
            nome      TEXT    NOT NULL,
            faixa     TEXT    NOT NULL,
            academia  TEXT    NOT NULL
        )
    """)
    conn.commit()
    conn.close()

def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    return conn

@app.route("/atletas", methods=["GET"])
def listar_atletas():
    """
    Lista todos os atletas
    ---
    responses:
      200:
        description: Lista de atletas
    """
    conn = get_db()
    atletas = conn.execute("SELECT * FROM atletas ORDER BY nome").fetchall()
    conn.close()
    return jsonify([dict(a) for a in atletas]), 200

@app.route("/atletas/<int:id>", methods=["GET"])
def buscar_atleta(id):
    """
    Busca atleta por ID
    ---
    parameters:
      - name: id
        in: path
        type: integer
        required: true
    responses:
      200:
        description: Atleta encontrado
      404:
        description: Atleta não encontrado
    """
    conn = get_db()
    atleta = conn.execute("SELECT * FROM atletas WHERE id = ?", (id,)).fetchone()
    conn.close()
    if atleta is None:
        return jsonify({"erro": "Atleta não encontrado"}), 404
    return jsonify(dict(atleta)), 200

@app.route("/atletas", methods=["POST"])
def cadastrar_atleta():
    """
    Cadastra um novo atleta
    ---
    parameters:
      - name: body
        in: body
        required: true
        schema:
          properties:
            nome:
              type: string
              example: João Silva
            faixa:
              type: string
              example: Azul
            academia:
              type: string
              example: Gracie Barra
    responses:
      201:
        description: Atleta cadastrado
    """
    dados = request.get_json()
    nome     = dados.get("nome", "").strip()
    faixa    = dados.get("faixa", "").strip()
    academia = dados.get("academia", "").strip()
    if not nome or not faixa or not academia:
        return jsonify({"erro": "Campos obrigatórios"}), 400
    conn = get_db()
    cursor = conn.execute(
        "INSERT INTO atletas (nome, faixa, academia) VALUES (?, ?, ?)",
        (nome, faixa, academia)
    )
    conn.commit()
    novo_id = cursor.lastrowid
    conn.close()
    return jsonify({"id": novo_id, "nome": nome, "faixa": faixa, "academia": academia}), 201

@app.route("/atletas/<int:id>", methods=["PUT"])
def editar_atleta(id):
    """
    Edita um atleta existente
    ---
    parameters:
      - name: id
        in: path
        type: integer
        required: true
      - name: body
        in: body
        required: true
        schema:
          properties:
            nome:
              type: string
            faixa:
              type: string
            academia:
              type: string
    responses:
      200:
        description: Atleta atualizado
      404:
        description: Atleta não encontrado
    """
    dados = request.get_json()
    nome     = dados.get("nome", "").strip()
    faixa    = dados.get("faixa", "").strip()
    academia = dados.get("academia", "").strip()
    if not nome or not faixa or not academia:
        return jsonify({"erro": "Campos obrigatórios"}), 400
    conn = get_db()
    result = conn.execute(
        "UPDATE atletas SET nome=?, faixa=?, academia=? WHERE id=?",
        (nome, faixa, academia, id)
    )
    conn.commit()
    conn.close()
    if result.rowcount == 0:
        return jsonify({"erro": "Atleta não encontrado"}), 404
    return jsonify({"id": id, "nome": nome, "faixa": faixa, "academia": academia}), 200

@app.route("/atletas/<int:id>", methods=["DELETE"])
def excluir_atleta(id):
    """
    Exclui um atleta
    ---
    parameters:
      - name: id
        in: path
        type: integer
        required: true
    responses:
      200:
        description: Atleta excluído
      404:
        description: Atleta não encontrado
    """
    conn = get_db()
    result = conn.execute("DELETE FROM atletas WHERE id = ?", (id,))
    conn.commit()
    conn.close()
    if result.rowcount == 0:
        return jsonify({"erro": "Atleta não encontrado"}), 404
    return jsonify({"mensagem": "Atleta excluído com sucesso"}), 200

if __name__ == "__main__":
    init_db()
    print("✅  API rodando em http://0.0.0.0:5000")
    print("📖  Swagger em http://localhost:5000/apidocs")
    app.run(debug=True, host="0.0.0.0", port=5000)