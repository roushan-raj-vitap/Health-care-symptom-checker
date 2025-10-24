import { useState } from 'react'
import api from './api'

export default function SymptomForm({ onResult }) {
  const [symptoms, setSymptoms] = useState('')
  const [age, setAge] = useState('')
  const [sex, setSex] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  async function submit(e) {
    e.preventDefault()
    setLoading(true)
    setError('')
    try {
      const payload = {
        symptoms,
        age: age ? parseInt(age, 10) : null,
        sex: sex || null,
        existingConditions: null,
      }
      const { data } = await api.post('/api/symptoms', payload)
      onResult(data)
    } catch (err) {
      let msg = 'Something went wrong.'
      if (err.response?.data) {
        // backend might send a body
        msg = typeof err.response.data === 'string'
          ? err.response.data
          : (err.response.data.message || JSON.stringify(err.response.data))
      } else if (err.message) {
        msg = err.message
      }
      onResult({
        disclaimer: 'Service temporarily unavailable.',
        probableConditions: [],
        recommendedNextSteps: [],
        rawModelText: msg
      })
      setError(msg)
    } finally {
      setLoading(false)
    }
  }

  return (
    <form onSubmit={submit} style={styles.card}>
      <h2>Healthcare Symptom Checker</h2>

      <label style={styles.lbl}>Symptoms *</label>
      <textarea
        rows={5}
        value={symptoms}
        onChange={(e) => setSymptoms(e.target.value)}
        placeholder="Describe your symptoms..."
        required
        style={styles.textarea}
      />

      <div style={styles.row}>
        <div style={styles.col}>
          <label style={styles.lbl}>Age</label>
          <input
            type="number"
            value={age}
            onChange={(e) => setAge(e.target.value)}
            placeholder="e.g., 28"
            style={styles.input}
          />
        </div>
        <div style={styles.col}>
          <label style={styles.lbl}>Sex</label>
          <input
            value={sex}
            onChange={(e) => setSex(e.target.value)}
            placeholder="male / female / other"
            style={styles.input}
          />
        </div>
      </div>

      <button type="submit" disabled={loading} style={styles.button}>
        {loading ? 'Analyzing…' : 'Analyze'}
      </button>

      {error && <div style={styles.error}>Error: {error}</div>}
    </form>
  )
}

const styles = {
  card: { maxWidth: 720, margin: '24px auto', padding: 16, border: '1px solid #eee', borderRadius: 12 },
  lbl: { display: 'block', marginBottom: 6, fontWeight: 600 },
  textarea: { width: '100%', padding: 10, borderRadius: 8, border: '1px solid #ddd' },
  row: { display: 'flex', gap: 12, marginTop: 10 },
  col: { flex: 1 },
  input: { width: '100%', padding: 10, borderRadius: 8, border: '1px solid #ddd' },
  button: { marginTop: 14, padding: '10px 16px', borderRadius: 10, border: 'none', cursor: 'pointer' },
  error: { marginTop: 10, color: '#b00020' }
}
