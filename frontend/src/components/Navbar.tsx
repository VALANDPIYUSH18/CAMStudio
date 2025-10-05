import { Link } from 'react-router-dom'
import { Home, Info } from 'lucide-react'

export function Navbar() {
  return (
    <nav className="bg-white shadow-lg">
      <div className="container mx-auto px-4">
        <div className="flex justify-between items-center py-4">
          <div className="flex items-center space-x-2">
            <div className="w-8 h-8 bg-primary-500 rounded-lg flex items-center justify-center">
              <span className="text-white font-bold text-sm">F</span>
            </div>
            <span className="text-xl font-bold text-gray-800">Fullstack App</span>
          </div>
          
          <div className="flex space-x-6">
            <Link 
              to="/" 
              className="flex items-center space-x-1 text-gray-600 hover:text-primary-600 transition-colors"
            >
              <Home size={20} />
              <span>Home</span>
            </Link>
            <Link 
              to="/about" 
              className="flex items-center space-x-1 text-gray-600 hover:text-primary-600 transition-colors"
            >
              <Info size={20} />
              <span>About</span>
            </Link>
          </div>
        </div>
      </div>
    </nav>
  )
}