import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Heart, Filter } from 'lucide-react';
import { RecipeCard } from '../../components/RecipeCard';
import { Recipe } from '../../services/api';
import { categories } from '../../data/recipes';
import styles from './CategoriesPage.module.css';

interface CategoriesPageProps {
  recipes: Recipe[];
  onFavorite: (id: number) => void;
}

export function CategoriesPage({ recipes, onFavorite }: CategoriesPageProps) {
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);
  const navigate = useNavigate();

  const filteredRecipes = selectedCategory
    ? recipes.filter((r) => r.category === selectedCategory)
    : recipes;

  return (
    <div className={styles.page}>
      <div className={styles.container}>
        {/* Sidebar */}
        <aside className={styles.sidebar}>
          <div className={styles.sidebarHeader}>
            <Filter size={20} />
            <h2>分类</h2>
          </div>
          <ul className={styles.categoryList}>
            <li>
              <button
                className={`${styles.categoryItem} ${!selectedCategory ? styles.active : ''}`}
                onClick={() => setSelectedCategory(null)}
              >
                <span className={styles.categoryName}>全部食谱</span>
                <span className={styles.categoryCount}>{recipes.length}</span>
              </button>
            </li>
            {categories.map((category) => {
              const count = recipes.filter((r) => r.category === category.id).length;
              return (
                <li key={category.id}>
                  <button
                    className={`${styles.categoryItem} ${
                      selectedCategory === category.id ? styles.active : ''
                    }`}
                    onClick={() => setSelectedCategory(category.id)}
                  >
                    <span className={styles.categoryName}>{category.name}</span>
                    <span className={styles.categoryCount}>{count}</span>
                  </button>
                </li>
              );
            })}
          </ul>
        </aside>

        {/* Main Content */}
        <main className={styles.main}>
          <div className={styles.header}>
            <h1 className={styles.title}>
              {selectedCategory
                ? categories.find((c) => c.id === selectedCategory)?.name
                : '全部食谱'}
            </h1>
            <span className={styles.count}>{filteredRecipes.length} 个食谱</span>
          </div>

          {filteredRecipes.length > 0 ? (
            <div className={styles.grid}>
              {filteredRecipes.map((recipe, index) => (
                <div
                  key={recipe.id}
                  className={styles.cardWrapper}
                  style={{ animationDelay: `${index * 80}ms` }}
                >
                  <RecipeCard
                    recipe={recipe}
                    onFavorite={onFavorite}
                    onClick={() => navigate(`/recipe/${recipe.id}`)}
                  />
                </div>
              ))}
            </div>
          ) : (
            <div className={styles.emptyState}>
              <Heart size={48} className={styles.emptyIcon} />
              <h3>该分类下暂无食谱</h3>
              <p>敬请期待更多精彩内容</p>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}
