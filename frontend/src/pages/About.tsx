export function About() {
  return (
    <div className="max-w-4xl mx-auto">
      <div className="text-center mb-12">
        <h1 className="text-4xl font-bold text-gray-900 mb-4">
          About This Project
        </h1>
        <p className="text-xl text-gray-600 mb-8">
          A modern fullstack web application built with best practices
        </p>
      </div>

      <div className="bg-white rounded-lg shadow-md p-8">
        <h2 className="text-2xl font-semibold text-gray-800 mb-6">
          Project Structure
        </h2>
        
        <div className="grid md:grid-cols-2 gap-8">
          <div>
            <h3 className="text-lg font-semibold text-gray-700 mb-3">Frontend</h3>
            <div className="bg-gray-50 rounded-lg p-4 font-mono text-sm">
              <div>frontend/</div>
              <div className="ml-4">├── src/</div>
              <div className="ml-8">├── components/</div>
              <div className="ml-8">├── pages/</div>
              <div className="ml-8">├── services/</div>
              <div className="ml-8">├── types/</div>
              <div className="ml-8">└── utils/</div>
              <div className="ml-4">├── package.json</div>
              <div className="ml-4">├── vite.config.ts</div>
              <div className="ml-4">└── tailwind.config.js</div>
            </div>
          </div>

          <div>
            <h3 className="text-lg font-semibold text-gray-700 mb-3">Backend</h3>
            <div className="bg-gray-50 rounded-lg p-4 font-mono text-sm">
              <div>backend/</div>
              <div className="ml-4">├── src/</div>
              <div className="ml-8">├── routes/</div>
              <div className="ml-8">├── middleware/</div>
              <div className="ml-8">├── services/</div>
              <div className="ml-8">└── types/</div>
              <div className="ml-4">├── package.json</div>
              <div className="ml-4">└── tsconfig.json</div>
            </div>
          </div>
        </div>

        <div className="mt-8">
          <h3 className="text-lg font-semibold text-gray-700 mb-4">Getting Started</h3>
          <div className="bg-gray-50 rounded-lg p-4">
            <p className="text-gray-600 mb-4">To run the project:</p>
            <div className="font-mono text-sm space-y-1">
              <div># Install all dependencies</div>
              <div className="text-blue-600">npm run install:all</div>
              <div className="mt-2"># Start development servers</div>
              <div className="text-blue-600">npm run dev</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}