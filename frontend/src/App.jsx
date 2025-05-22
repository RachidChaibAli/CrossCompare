import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

function App() {
  const { data: apiResponse, error, isLoading } = useQuery(['apiResponse'], async () => {
    const response = await axios.get('http://api-gateway:8080/hola'); // Cambia la URL si es necesario
    return response.data;
  });

  if (isLoading) return <p>Cargando...</p>;
  if (error) return <p>Error al obtener la respuesta de la API: {error.message}</p>;

  return (
    <>
      <h1>Prueba</h1>
      <div>
        <h2>API Response:</h2>
        <p>{apiResponse}</p>
      </div>
    </>
  );
}

export default App;
