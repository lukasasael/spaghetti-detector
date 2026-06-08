import json
import numpy as np
import os
from sklearn.linear_model import LinearRegression

def treinar_pesos_legibilidade():
    # Caminho do arquivo JSON que simula o dataset de calibração
    dataset_path = os.path.join('data', 'mock_dataset.json')
    
    if not os.path.exists(dataset_path):
        # Gerando um arquivo mock caso ele não exista, simulação de 4 métodos com notas humanas
        mock_data = [
            {"exits": 1, "depth": 1, "jumps": 0, "human_score": 9.5}, # Código limpo/linear
            {"exits": 4, "depth": 5, "jumps": 1, "human_score": 2.1}, # Código muito espaguete
            {"exits": 2, "depth": 3, "jumps": 2, "human_score": 4.8}, # Média complexidade
            {"exits": 1, "depth": 2, "jumps": 0, "human_score": 8.0}  # Complexidade baixa
        ]
        os.makedirs(os.path.dirname(dataset_path), exist_ok=True)
        with open(dataset_path, 'w') as f:
            json.dump(mock_data, f)

    # 1. Carrega o dataset de treino
    with open(dataset_path, 'r') as f:
        dataset = json.load(f)

    X = []
    y = []

    for item in dataset:
        E = item['exits']
        D = item['depth']
        J = item['jumps']
        
        # Aplicamos a transformação quadrática diretamente no X antes do treino
        # Isso ensina o modelo que o impacto do aninhamento cresce ao quadrado
        X.append([E, D**2, J])
        
        # A regressão linear tenta aproximar: penalidade = 10 - nota_humana
        penalidade_humana = 10 - item['human_score']
        y.append(penalidade_humana)

    X = np.array(X)
    y = np.array(y)

    # 2. Treina a Regressão Linear Múltipla (Sem intercepto para fixar a nota máxima em 10)
    model = LinearRegression(fit_intercept=False)
    model.fit(X, y)

    # 3. Extrai os pesos aprendidos automaticamente
    w1, w2, w3 = model.coef_
    
    print("=== TREINAMENTO CONCLUÍDO (Scikit-Learn) ===")
    print(f"Pesos aprendidos com base na percepção humana:")
    print(f"  -> Peso para Saídas (w1): {round(w1, 4)}")
    print(f"  -> Peso para Aninhamento Quadrático (w2): {round(w2, 4)}")
    print(f"  -> Peso para Jumps (w3): {round(w3, 4)}\n")
    
    return w1, w2, w3

if __name__ == "__main__":
    treinar_pesos_legibilidade()
    