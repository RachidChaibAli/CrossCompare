import { useEffect, useState } from 'react';

function App() {
  const [apiResponse, setApiResponse] = useState('');

  useEffect(() => {
    fetch('http://api-gateway:8080/hola') // Cambia la URL si es necesario
      .then((response) => response.text())
      .then((data) => setApiResponse(data))
      .catch((error) => console.error('Error fetching API:', error));
  }, []);

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
