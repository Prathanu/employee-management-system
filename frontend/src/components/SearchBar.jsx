/**
 * SearchBar — reusable search input for employee list.
 */
function SearchBar({ value, onChange, onSearch, placeholder = 'Search by name, email, department...' }) {
  const handleSubmit = (e) => {
    e.preventDefault()
    onSearch(value)
  }

  return (
    <form className="row g-2" onSubmit={handleSubmit}>
      <div className="col-md-8">
        <div className="input-group">
          <span className="input-group-text">
            <i className="bi bi-search"></i>
          </span>
          <input
            type="text"
            className="form-control"
            placeholder={placeholder}
            value={value}
            onChange={(e) => onChange(e.target.value)}
          />
        </div>
      </div>
      <div className="col-md-4 d-flex gap-2">
        <button type="submit" className="btn btn-primary">
          Search
        </button>
        <button
          type="button"
          className="btn btn-outline-secondary"
          onClick={() => {
            onChange('')
            onSearch('')
          }}
        >
          Clear
        </button>
      </div>
    </form>
  )
}

export default SearchBar
