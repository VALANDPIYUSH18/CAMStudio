import { useParams } from 'react-router-dom'
import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { api } from '../../services/api'
import { 
  Heart, 
  ShoppingCart, 
  Download,
  Check,
  X,
  Camera
} from 'lucide-react'

interface Photo {
  id: string
  filename: string
  thumbnailUrl: string
  previewUrl?: string
  originalUrl: string
  isSelected: boolean
  price?: number
  uploadedAt: string
}

interface GalleryData {
  orderId: string
  photos: Photo[]
  totalPhotos: number
  selectionLimit?: number
  clientInfo?: {
    name: string
    email: string
    phone?: string
  }
}

export default function PublicGalleryPage() {
  const { orderId } = useParams<{ orderId: string }>()
  const [selectedPhotos, setSelectedPhotos] = useState<Set<string>>(new Set())
  const [showSelection, setShowSelection] = useState(false)

  const { data: gallery, isLoading } = useQuery<GalleryData>({
    queryKey: ['gallery', orderId],
    queryFn: async () => {
      const response = await api.get(`/gallery/${orderId}/public`)
      return response.data
    },
    enabled: !!orderId
  })

  const handlePhotoSelect = (photoId: string) => {
    const newSelected = new Set(selectedPhotos)
    if (newSelected.has(photoId)) {
      newSelected.delete(photoId)
    } else {
      newSelected.add(photoId)
    }
    setSelectedPhotos(newSelected)
  }

  const handleProceedToPayment = () => {
    // This would typically redirect to payment page
    console.log('Proceeding to payment with selected photos:', Array.from(selectedPhotos))
  }

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto"></div>
          <p className="mt-4 text-gray-600">Loading gallery...</p>
        </div>
      </div>
    )
  }

  if (!gallery) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <div className="text-center">
          <Camera className="mx-auto h-12 w-12 text-gray-400" />
          <h3 className="mt-2 text-lg font-medium text-gray-900">Gallery not found</h3>
          <p className="mt-1 text-sm text-gray-500">
            The gallery you're looking for doesn't exist or is no longer available.
          </p>
        </div>
      </div>
    )
  }

  const totalPrice = gallery.photos
    .filter(photo => selectedPhotos.has(photo.id))
    .reduce((sum, photo) => sum + (photo.price || 0), 0)

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900">Photo Gallery</h1>
              <p className="text-sm text-gray-500">
                {gallery.totalPhotos} photos available
                {gallery.selectionLimit && ` • Select up to ${gallery.selectionLimit}`}
              </p>
            </div>
            <div className="flex items-center space-x-4">
              {selectedPhotos.size > 0 && (
                <div className="text-sm text-gray-600">
                  {selectedPhotos.size} selected • ${totalPrice.toFixed(2)}
                </div>
              )}
              <button
                onClick={() => setShowSelection(!showSelection)}
                className="inline-flex items-center px-4 py-2 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700"
              >
                <ShoppingCart className="h-4 w-4 mr-2" />
                {showSelection ? 'Hide Selection' : 'View Selection'}
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {gallery.photos.length === 0 ? (
          <div className="text-center py-12">
            <Camera className="mx-auto h-12 w-12 text-gray-400" />
            <h3 className="mt-2 text-lg font-medium text-gray-900">No photos available</h3>
            <p className="mt-1 text-sm text-gray-500">
              Photos haven't been uploaded to this gallery yet.
            </p>
          </div>
        ) : (
          <>
            {/* Photo Grid */}
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
              {gallery.photos.map((photo) => (
                <div
                  key={photo.id}
                  className="relative group cursor-pointer"
                  onClick={() => handlePhotoSelect(photo.id)}
                >
                  <div className="aspect-square relative overflow-hidden rounded-lg bg-gray-200">
                    <img
                      src={photo.thumbnailUrl}
                      alt={photo.filename}
                      className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-200"
                    />
                    
                    {/* Selection Overlay */}
                    <div className={`absolute inset-0 flex items-center justify-center transition-opacity duration-200 ${
                      selectedPhotos.has(photo.id) 
                        ? 'bg-blue-500 bg-opacity-75' 
                        : 'bg-black bg-opacity-0 group-hover:bg-opacity-50'
                    }`}>
                      {selectedPhotos.has(photo.id) ? (
                        <Check className="h-8 w-8 text-white" />
                      ) : (
                        <Heart className="h-6 w-6 text-white opacity-0 group-hover:opacity-100" />
                      )}
                    </div>

                    {/* Price Badge */}
                    {photo.price && (
                      <div className="absolute top-2 right-2 bg-white bg-opacity-90 px-2 py-1 rounded text-xs font-medium">
                        ${photo.price}
                      </div>
                    )}
                  </div>
                </div>
              ))}
            </div>

            {/* Selection Summary */}
            {showSelection && selectedPhotos.size > 0 && (
              <div className="mt-8 bg-white rounded-lg shadow p-6">
                <h3 className="text-lg font-medium text-gray-900 mb-4">
                  Selected Photos ({selectedPhotos.size})
                </h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
                  {gallery.photos
                    .filter(photo => selectedPhotos.has(photo.id))
                    .map((photo) => (
                      <div key={photo.id} className="relative">
                        <div className="aspect-square relative overflow-hidden rounded-lg bg-gray-200">
                          <img
                            src={photo.thumbnailUrl}
                            alt={photo.filename}
                            className="w-full h-full object-cover"
                          />
                          <button
                            onClick={() => handlePhotoSelect(photo.id)}
                            className="absolute top-2 right-2 bg-red-500 text-white rounded-full p-1 hover:bg-red-600"
                          >
                            <X className="h-3 w-3" />
                          </button>
                        </div>
                        {photo.price && (
                          <p className="mt-1 text-sm text-gray-600">${photo.price}</p>
                        )}
                      </div>
                    ))}
                </div>
                <div className="mt-6 flex justify-between items-center">
                  <div className="text-lg font-medium text-gray-900">
                    Total: ${totalPrice.toFixed(2)}
                  </div>
                  <button
                    onClick={handleProceedToPayment}
                    className="inline-flex items-center px-6 py-3 border border-transparent rounded-md shadow-sm text-base font-medium text-white bg-blue-600 hover:bg-blue-700"
                  >
                    <Download className="h-5 w-5 mr-2" />
                    Proceed to Payment
                  </button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}