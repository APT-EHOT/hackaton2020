import json

from flask import Flask, request, abort
import tsp
app = Flask(__name__)

@app.route('/')
def hello_world(mat):
    r = range(len(mat))
    # Dictionary of distance
    dist = {(i, j): mat[i][j] for i in r for j in r}
    a = tsp.tsp(r, dist)
    b = json.dumps({"answer": [str(x) for x in a[1][1:-1]]})
    print(b)
    return json.dumps({"answer": [str(x) for x in a[1][1:-1]]})

@app.route('/foo', methods=['POST'])
def foo():
    if not request.json:
        abort(400)
    y = json.loads(request.headers.get('huita'))
    matrix = []
    row = []
    for i in range(len(y['rows'])):
        for k in range(len(y['rows'])):
            row.append(y['rows'][k]['elements'][i]['duration']['value'])
        matrix.append(row)
    return hello_world(matrix)

if __name__ == '__main__':
    app.run()