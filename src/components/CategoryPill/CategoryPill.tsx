import { Sunrise, UtensilsCrossed, Moon, Cake, Soup, Cookie, LucideIcon } from 'lucide-react';
import { Category } from '../../types';
import styles from './CategoryPill.module.css';

interface CategoryPillProps {
  category: Category;
  isSelected: boolean;
  onClick: () => void;
}

const iconMap: Record<string, LucideIcon> = {
  Sunrise,
  UtensilsCrossed,
  Moon,
  Cake,
  Soup,
  Cookie,
};

export function CategoryPill({ category, isSelected, onClick }: CategoryPillProps) {
  const Icon = iconMap[category.icon] || UtensilsCrossed;

  return (
    <button
      className={`${styles.pill} ${isSelected ? styles.selected : ''}`}
      onClick={onClick}
    >
      <span className={styles.iconWrapper}>
        <Icon size={24} />
      </span>
      <span className={styles.name}>{category.name}</span>
    </button>
  );
}
