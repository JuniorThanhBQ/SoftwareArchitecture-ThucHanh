const express = require('express');
const cors = require('cors');
const { Pool } = require('pg');
const redis = require('redis');

const app = express();
app.use(cors());
app.use(express.json())

const pool = new Pool({
    connectionString: process.env.DATABASE_URL 
});

const redisClient = redis.createClient({
    url: process.env.REDIS_URL
});

redisClient.connect().catch(console.error);

app.get('/health', async (req, res) => {
    const health = {
        uptime: process.uptime(),
        database: 'unknown',
        cache: 'unknown' 
    };

    try{
        await pool.query('SELECT 1');
        health.database = 'connected';
    } catch (e){
        health.database = 'error';
    }

    try { 
        await redisClient.ping(); 
        health.cache = 'connected'; 
    } 
    catch (e) { 
        health.cache = 'error'; 
    } 

    res.json(health); 
})

app.get('/api/todos', async (req, res) => {
    try {
        const cached = await redisClient.get('todos');
        if (cached) {
            return res.json(JSON.parse(cached));
        }

        const result = await pool.query('SELECT * FROM todos ORDER BY id DESC');
        await redisClient.setEx('todos', 3600, JSON.stringify(result.rows));
        
        res.json(result.rows);
    } catch(error){
        res.status(500).json({error: error.message});
    }
});

app.post('/api/todos', async (req, res) => { 
    try { 
        const { title } = req.body; 
        const result = await pool.query('INSERT INTO todos (title) VALUES ($1) RETURNING *', [title]); 

        await redisClient.del('todos');

        res.status(201).json(result.rows[0]);
    } catch(error){
        res.status(500).json({error: error.message});
    }
});

app.patch('/api/todos/:id', async (req, res) => { 
    try {
        const { id } = req.params; 
        const { completed } = req.body;
        const result = await pool.query('UPDATE todos SET completed = $1 WHERE id = $2 RETURNING *', [completed, id]);
        
        if (result.rows.length === 0) { 
            return res.status(404).json({ error: 'Todo not found' }); 
        } 

        await redisClient.del('todos');

        res.json(result.rows[0]);
    } catch (error){
        res.status(500).json({error: error.message});
    }
});

app.delete('/api/todos/:id', async (req, res) => {
    try{
        const {id} = req.params;
        await pool.query('DELETE FROM todos WHERE id = $1', [id]); 
        await redisClient.del('todos'); 
        res.status(204).send(); 
    } catch(error){
        res.status(500).json({error: error.message});
    }
});

const PORT = process.env.PORT || 4000; 

app.listen(PORT, () => { 
    console.log(`Backend running on port ${PORT}`); 
}); 
