import React, { useState, useEffect } from 'react';
import axios from 'axios';

function App() {
  const [message, setMessage] = useState('Loading...');
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await axios.get(`${process.env.REACT_APP_API_URL}/api/health`);
        setMessage(response.data.message || 'Connected to backend!');
      } catch (err) {
        setError('Failed to connect to backend: ' + err.message);
      }
    };

    fetchData();
  }, []);

  return (
    <div className="App">
      <header className="App-header">
        <h1>OU</h1>
        {error ? (
          <p style={{ color: 'red' }}>{error}</p>
        ) : (
          <p>{message}</p>
        )}
      </header>
    </div>
  );
}

export default App;
