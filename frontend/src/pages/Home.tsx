import { useState, useEffect } from 'react'
import { apiService } from '../services/api'

export function Home() {
  const [message, setMessage] = useState<string>('')
  const [loading, setLoading] = useState<boolean>(false)

  const fetchMessage = async () => {
    setLoading(true)
    try {
      const data = await apiService.getHello()
      setMessage(data.message)
    } catch (error) {
      console.error('Error fetching message:', error)
      setMessage('Failed to connect to backend')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMessage()
  }, [])

  return (
    <div className="max-w-4xl mx-auto">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          Welcome to Fullstack App
        </h1>
        <p className="text-xl text-gray-600 mb-8">
          A modern React + Node.js application with TypeScript
        </p>
        
        <div className="bg-white rounded-lg shadow-md p-8 mb-8">
          <h2 className="text-2xl font-semibold text-gray-800 mb-4">
            Backend Connection Test
          </h2>
          {loading ? (
            <div className="flex items-center justify-center">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary-500"></div>
              <span className="ml-2 text-gray-600">Loading...</span>
            </div>
          ) : (
            <div>
              <p className="text-lg text-gray-700 mb-4">{message}</p>
              <button
                onClick={fetchMessage}
                className="bg-primary-500 hover:bg-primary-600 text-white px-6 py-2 rounded-lg transition-colors"
              >
                Refresh Message
              </button>
            </div>
          )}
        </div>

        <div className="grid md:grid-cols-2 gap-6">
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">
              Frontend Features
            </h3>
            <ul className="text-left text-gray-600 space-y-2">
              <li>• React 18 with TypeScript</li>
              <li>• Vite for fast development</li>
              <li>• Tailwind CSS for styling</li>
              <li>• React Router for navigation</li>
              <li>• Axios for API calls</li>
            </ul>
          </div>
          
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-xl font-semibold text-gray-800 mb-3">
              Backend Features
            </h3>
            <ul className="text-left text-gray-600 space-y-2">
              <li>• Node.js with Express</li>
              <li>• TypeScript support</li>
              <li>• CORS enabled</li>
              <li>• Security headers with Helmet</li>
              <li>• Request logging with Morgan</li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  )
}