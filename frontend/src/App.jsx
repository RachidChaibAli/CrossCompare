import axios from 'axios';
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useRef, useState } from 'react';

function App() {
  const [busqueda, setBusqueda] = useState('');
  const [resultados, setResultados] = useState([]);
  const [cargando, setCargando] = useState(false);
  const [error, setError] = useState(null);
  const intervalRef = useRef(null);

  const handleBuscar = async (e) => {
    e.preventDefault();
    setCargando(true);
    setResultados([]);
    setError(null);

    try {
      // POST inicial para lanzar la búsqueda
      await axios.post('http://localhost:8080/unificador/juegos', null, {
        params: { busqueda }
      });

      // Lanzar polling GET cada 2 segundos
      if (intervalRef.current) clearInterval(intervalRef.current);
      intervalRef.current = setInterval(async () => {
        try {
          const res = await axios.get('http://localhost:8080/unificador/juegos', {
            params: { busqueda }
          });
          setResultados(res.data);
        } catch (err) {
          setError('Error al obtener resultados');
          console.error(err);
        }
      }, 2000);
    } catch (err) {
      setError('Error al iniciar la búsqueda');
      console.error(err);
      setCargando(false);
    }
  };

  useEffect(() => {
    return () => clearInterval(intervalRef.current);
  }, []);

  return (
    <div className="container mt-5">
      <h1>Buscador de Juegos</h1>
      <form className="d-flex mb-4" onSubmit={handleBuscar}>
        <input
          className="form-control me-2"
          type="search"
          placeholder="Buscar juego"
          value={busqueda}
          onChange={e => setBusqueda(e.target.value)}
        />
        <button className="btn btn-primary" type="submit" disabled={cargando || !busqueda}>
          Buscar
        </button>
      </form>
      {error && <div className="alert alert-danger">{error}</div>}
      <ul className="list-group">
        {resultados.map((juego, idx) => (
          <li className="list-group-item" key={idx}>
            <span>
              <strong>{juego.nombre}</strong> - {juego.plataforma} - {juego.precio}€
            </span>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default App;
