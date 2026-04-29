import { Star } from 'lucide-react';
import './StarRating.css';

interface StarRatingProps {
  value: number;
  onChange?: (value: number) => void;
  readonly?: boolean;
  size?: 'small' | 'medium' | 'large';
}

export function StarRating({ value, onChange, readonly = false, size = 'medium' }: StarRatingProps) {
  const sizes = {
    small: 16,
    medium: 24,
    large: 32,
  };

  const starSize = sizes[size];

  const handleClick = (rating: number) => {
    if (!readonly && onChange) {
      onChange(rating);
    }
  };

  return (
    <div className={`star-rating ${readonly ? 'readonly' : ''} ${size}`}>
      {[1, 2, 3, 4, 5].map((rating) => (
        <button
          key={rating}
          type="button"
          className={`star ${value >= rating ? 'active' : ''}`}
          onClick={() => handleClick(rating)}
          disabled={readonly}
        >
          <Star
            size={starSize}
            fill={value >= rating ? '#fbbf24' : 'none'}
            stroke="#fbbf24"
          />
        </button>
      ))}
    </div>
  );
}
