import json
import numpy as np
import os
from train import treinar_pesos_legibilidade

def calcular_nota_real(method, w1, w2, w3):
    E = method['exits']
    D = method['depth']
    J = method['jumps']
    
    # Aplicação da fórmula utilizando os coeficientes aprendidos pelo Scikit-Learn
    penalidade = (w1 * E) + (w2 * (D ** 2)) + (w3 * J)
    
    nota = 10 - penalidade
    return max(0.0, min(10.0, round(nota, 2)))

def analisar_codigo_java(json_filename, w1, w2, w3):
    json_path = os.path.join('data', json_filename)
    
    with open(json_path, 'r', encoding='utf-8') as file:
        data = json.load(file)
        
    print(f"=== DETECTOR DE CÓDIGO ESPAGUETE: {data['analyzedFile']} ===")
    
    for method in data['methods']:
        nota = calcular_nota_real(method, w1, w2, w3)
        print(f"Método: {method['methodName']} -> Nota: {nota}/10")

if __name__ == "__main__":
    # Primeiro o sistema aprende os pesos com base no histórico
    w1, w2, w3 = treinar_pesos_legibilidade()
    
    # Depois o sistema analisa o arquivo JSON gerado pelo ecossistema Java
    # (Para testar, coloque o JSON exportado pelo Java dentro da pasta core-analyzer/data/)
    analisar_codigo_java('input_sample.json', w1, w2, w3)
