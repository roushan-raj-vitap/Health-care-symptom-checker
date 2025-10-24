export default function Result({ data }) {
  if (!data) return null

  const { disclaimer, probableConditions = [], recommendedNextSteps = [], rawModelText } = data

  return (
    <div style={styles.card}>
      {disclaimer && <p><strong>Disclaimer:</strong> {disclaimer}</p>}

      {probableConditions.length > 0 && (
        <>
          <h3>Possible conditions</h3>
          <ul>
            {probableConditions.map((c, i) => <li key={i}>{c}</li>)}
          </ul>
        </>
      )}

      {recommendedNextSteps.length > 0 && (
        <>
          <h3>Recommended next steps</h3>
          <ol>
            {recommendedNextSteps.map((s, i) => <li key={i}>{s}</li>)}
          </ol>
        </>
      )}

      <details style={{ marginTop: 8 }}>
        <summary>Raw model text</summary>
        <pre style={{ whiteSpace: 'pre-wrap' }}>{rawModelText}</pre>
      </details>
    </div>
  )
}

const styles = {
  card: { maxWidth: 720, margin: '16px auto', padding: 16, border: '1px solid #eee', borderRadius: 12 }
}
