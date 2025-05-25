import axios from 'axios';
// Se ha restaurado la importación de Bootstrap CSS.
import 'bootstrap/dist/css/bootstrap.min.css';
import { useEffect, useState } from 'react';

function App() {
  // Estado para el término de búsqueda ingresado por el usuario
  const [busqueda, setBusqueda] = useState('');
  // Estado para almacenar los resultados de la búsqueda de juegos
  const [resultados, setResultados] = useState([]);
  // Estado para indicar si la aplicación está cargando datos (principalmente para la búsqueda inicial)
  const [cargando, setCargando] = useState(false);
  // Estado para almacenar cualquier mensaje de error
  const [error, setError] = useState(null);
  // Estado para controlar si se ha realizado una búsqueda (para mostrar el mensaje de no resultados)
  const [busquedaRealizada, setBusquedaRealizada] = useState(false);


  /**
   * Función asíncrona para obtener los resultados de los juegos.
   * Realiza una petición GET al backend.
   * @param {string} searchTerm - El término de búsqueda actual.
   */
  const fetchResults = async (searchTerm) => {
    try {
      const res = await axios.get('http://localhost:8080/unificador/juegos', {
        params: { busqueda: searchTerm }
      });
      setResultados(res.data);
      setError(null); // Limpia cualquier error anterior si la petición fue exitosa
    } catch (err) {
      setError('Error al obtener resultados. Inténtalo de nuevo más tarde.');
      console.error('Error al obtener resultados:', err);
    }
  };

  /**
   * Maneja el envío del formulario de búsqueda.
   * Inicia la búsqueda POST en el backend y luego actualiza los resultados.
   * @param {Event} e - El evento del formulario.
   */
  const handleBuscar = async (e) => {
    e.preventDefault(); // Previene el comportamiento por defecto del formulario (recargar la página)
    setCargando(true); // Establece el estado de carga a verdadero para la búsqueda inicial
    setResultados([]); // Limpia los resultados anteriores
    setError(null); // Limpia cualquier error anterior
    setBusquedaRealizada(true); // Marca que se ha realizado una búsqueda

    try {
      // Realiza la solicitud POST inicial para lanzar la búsqueda en el backend
      await axios.post('http://localhost:8080/unificador/juegos', null, {
        params: { busqueda } // Pasa el término de búsqueda como parámetro
      });
      // Después de la petición POST, se realiza una primera obtención de resultados
      await fetchResults(busqueda);
    } catch (err) {
      setError('Error al iniciar la búsqueda. Asegúrate de que el servidor está funcionando.');
      console.error('Error al iniciar la búsqueda:', err);
    } finally {
      setCargando(false); // Desactiva el estado de carga una vez que la búsqueda inicial ha terminado
    }
  };

  // useEffect para el polling continuo de resultados
  useEffect(() => {
    // Realiza una primera obtención de resultados cuando el componente se monta o la búsqueda cambia
    fetchResults(busqueda);

    // Configura el intervalo para obtener resultados cada 4 segundos
    const intervalId = setInterval(() => {
      fetchResults(busqueda);
    }, 4000); // Polling cada 4 segundos

    // Función de limpieza para detener el intervalo cuando el componente se desmonta
    // o cuando 'busqueda' cambia y se re-ejecuta este efecto
    return () => {
      if (intervalId) {
        clearInterval(intervalId); // Asegura que el intervalo se detenga para evitar fugas de memoria
      }
    };
  }, [busqueda]); // El efecto se re-ejecuta si el término de búsqueda cambia

  return (
    <div className="container-fluid mt-5" style={{ minHeight: '100vh' }}>
      <h1 className="text-center mb-4 mt-4 text-primary">CrossCompare</h1>
      <div className="row justify-content-center mb-4">
        <div className="col-12 col-md-8 col-lg-6 d-flex justify-content-center">
          <form className="d-flex justify-content-center w-100" onSubmit={handleBuscar}>
            <input
              className="form-control me-2 rounded-pill shadow-sm"
              type="search"
              placeholder="Buscar juego por nombre..."
              value={busqueda}
              onChange={e => setBusqueda(e.target.value)}
              aria-label="Buscar juego"
              style={{ width: '100%', minWidth: '250px', maxWidth: '400px' }}
            />
            <button
              className="btn btn-primary rounded-pill shadow-sm"
              type="submit"
              disabled={cargando || !busqueda.trim()}
            >
              {cargando ? (
                <span className="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
              ) : (
                'Buscar'
              )}
            </button>
          </form>
        </div>
      </div>

      {/* Mensaje de error */}
      {error && (
        <div className="alert alert-danger text-center rounded-3 shadow-sm" role="alert">
          {error}
        </div>
      )}

      {/* Indicador de carga */}
      {cargando && resultados.length === 0 && (
        <div className="text-center my-5">
          <div className="spinner-border text-primary" role="status">
            <span className="visually-hidden">Cargando...</span>
          </div>
          <p className="mt-2 text-muted">Buscando juegos...</p>
        </div>
      )}

      {/* Mostrar resultados o mensaje de no resultados */}
      {!cargando && resultados.length === 0 && busquedaRealizada && (
        <div className="alert alert-info text-center rounded-3 shadow-sm" role="alert">
          No se encontraron resultados para "{busqueda}".
        </div>
      )}

      {/* Lista de resultados de juegos */}
      <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
        {resultados.map((juegoData, idx) => (
          <div className="col" key={idx}>
            <div className="card h-100 shadow-lg border-0 rounded-4 overflow-hidden">
              {/* Imagen del boxart del juego */}
              <img
                src={juegoData.videojuego.boxartUrl || 'https://placehold.co/177x177/cccccc/333333?text=No+Image'}
                className="card-img-top img-fluid"
                alt={`Boxart de ${juegoData.videojuego.nombre}`}
                onError={(e) => {
                  e.target.onerror = null; // Evita bucles infinitos
                  e.target.src = 'https://placehold.co/177x177/cccccc/333333?text=No+Image'; // Imagen de fallback
                }}
                style={{ objectFit: 'cover', height: '250px', width: '100%' }} // Altura ajustada a 250px
              />
              <div className="card-body d-flex flex-column">
                {/* Nombre del juego */}
                <h5 className="card-title text-truncate text-primary">{juegoData.videojuego.nombre}</h5>
                {/* Desarrolladora y Fecha de Lanzamiento */}
                <h6 className="card-subtitle mb-2 text-muted">
                  {juegoData.videojuego.desarrolladora}
                  {juegoData.videojuego.fechalanzamiento && ` - ${new Date(juegoData.videojuego.fechalanzamiento).getFullYear()}`}
                </h6>
                {/* Descripción del juego (con scroll vertical) */}
                <p className="card-text text-secondary mb-3" style={{ maxHeight: '100px', overflowY: 'auto' }}>
                  {juegoData.videojuego.descripcion}
                </p>
                {/* Precios por plataforma */}
                <div className="mt-auto"> {/* Empuja el contenido hacia abajo */}
                  <h6 className="text-info mb-2">Precios:</h6>
                  <ul className="list-group list-group-flush border-top pt-2">
                    {Object.entries(juegoData.preciosPorPlataforma).map(([plataforma, precio]) => (
                      <li className="list-group-item d-flex justify-content-between align-items-center px-0 py-1" key={plataforma}>
                        <span className="text-dark">{plataforma}:</span>
                        <span className="badge bg-success rounded-pill fs-6">{precio}€</span>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default App;
