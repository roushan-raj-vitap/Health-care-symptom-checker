import { useEffect, useState } from 'react'
import api from './api'

export default function History() {
  const [items, setItems] = useState([])
  const [error, setError] = useState('')

  useEffect(() => {
    let ignore = false
    async function load() {
      try {
        const { data } = await api.get('/api/symptoms/history')
        if (!ignore) setItems(data || [])
      } catch (e) {
        setError('Failed to load history (are you logged in?)')
      }
    }
    load()
    return () => { ignore = true }
  }, [])

  if (error) return <div style={styles.card}>{error}</div>

  return (
    <div style={styles.card}>
      <h3>Your Symptom Checks</h3>
      {items.length === 0 ? (
        <p>No history yet.</p>
      ) : (
        <ul>
          {items.map((h) => (
            <li key={h.id} style={{ marginBottom: 8 }}>
              <div><strong>Asked:</strong> <span style={{ whiteSpace:'pre-wrap' }}>{h.requestText}</span></div>
              <details>
                <summary>Response</summary>
                <pre style={{ whiteSpace: 'pre-wrap' }}>{h.responseText}</pre>
              </details>
            </li>
          ))}
        </ul>
      )}
    </div>
  )
}

const styles = {
  card: { maxWidth: 720, margin: '16px auto', padding: 16, border: '1px solid #eee', borderRadius: 12 }
}
