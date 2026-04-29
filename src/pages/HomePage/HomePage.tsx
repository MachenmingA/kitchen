import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Heart, ArrowRight, ChevronLeft, ChevronRight } from 'lucide-react';
import { RecipeCard } from '../../components/RecipeCard';
import { CategoryPill } from '../../components/CategoryPill';
import { Recipe } from '../../services/api';
import { categories } from '../../data/recipes';
import styles from './HomePage.module.css';

interface HomePageProps {
  recipes: Recipe[];
  onFavorite: (id: number) => void;
  searchQuery: string;
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

export function HomePage({ recipes, onFavorite, searchQuery, currentPage, totalPages, onPageChange }: HomePageProps) {
  const [selectedCategory, setSelectedCategory] = useState<string | null>(null);

  const filteredRecipes = recipes.filter((recipe) => {
    const matchesSearch = searchQuery
      ? recipe.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
        recipe.description.toLowerCase().includes(searchQuery.toLowerCase())
      : true;
    const matchesCategory = selectedCategory
      ? recipe.category === selectedCategory
      : true;
    return matchesSearch && matchesCategory;
  });

  const featuredRecipes = recipes.slice(0, 3);
  const popularRecipes = [...recipes].sort((a, b) => b.favoritesCount - a.favoritesCount).slice(0, 6);

  return (
    <div className={styles.page}>
      {/* Hero Section */}
      <section className={styles.hero}>
        <div className={styles.heroContent}>
          <h1 className={styles.heroTitle}>发现美食，分享快乐</h1>
          <p className={styles.heroSubtitle}>
            探索千种美味食谱，找到你最喜欢的味道
          </p>
        </div>
        <div className={styles.heroImages}>
          {featuredRecipes.map((recipe) => (
            <Link
              key={recipe.id}
              to={`/recipe/${recipe.id}`}
              className={styles.heroImageWrapper}
            >
              <img src={recipe.imageUrl} alt={recipe.title} className={styles.heroImage} />
              <span className={styles.heroImageLabel}>{recipe.title}</span>
            </Link>
          ))}
        </div>
      </section>

      {/* Categories */}
      <section className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>探索分类</h2>
          <Link to="/categories" className={styles.sectionLink}>
            查看全部 <ArrowRight size={16} />
          </Link>
        </div>
        <div className={styles.categories}>
          {categories.map((category) => (
            <CategoryPill
              key={category.id}
              category={category}
              isSelected={selectedCategory === category.id}
              onClick={() =>
                setSelectedCategory(
                  selectedCategory === category.id ? null : category.id
                )
              }
            />
          ))}
        </div>
      </section>

      {/* Recipe Grid */}
      <section className={styles.section}>
        <div className={styles.sectionHeader}>
          <h2 className={styles.sectionTitle}>
            {selectedCategory
              ? categories.find((c) => c.id === selectedCategory)?.name + '食谱'
              : searchQuery
              ? '搜索结果'
              : '热门食谱'}
          </h2>
          {selectedCategory && (
            <button
              className={styles.clearFilter}
              onClick={() => setSelectedCategory(null)}
            >
              清除筛选
            </button>
          )}
        </div>

        {filteredRecipes.length > 0 ? (
          <div className={styles.recipeGrid}>
            {filteredRecipes.map((recipe, index) => (
              <div
                key={recipe.id}
                className={styles.recipeCardWrapper}
                style={{ animationDelay: `${index * 100}ms` }}
              >
                <RecipeCard
                  recipe={recipe}
                  onFavorite={onFavorite}
                  onClick={() => {}}
                />
              </div>
            ))}
          </div>
        ) : (
          <div className={styles.emptyState}>
            <Heart size={48} className={styles.emptyIcon} />
            <h3>暂无符合条件的食谱</h3>
            <p>试试其他关键词或分类吧</p>
          </div>
        )}
      </section>

      {/* Popular Section */}
      {!searchQuery && !selectedCategory && (
        <section className={styles.section}>
          <div className={styles.sectionHeader}>
            <h2 className={styles.sectionTitle}>最受欢迎的食谱</h2>
          </div>
          <div className={styles.popularGrid}>
            {popularRecipes.map((recipe, index) => (
              <div key={recipe.id} className={styles.popularItem}>
                <span className={styles.popularRank}>#{index + 1}</span>
                <Link to={`/recipe/${recipe.id}`} className={styles.popularImageWrapper}>
                  <img src={recipe.imageUrl} alt={recipe.title} className={styles.popularImage} />
                </Link>
                <div className={styles.popularInfo}>
                  <Link to={`/recipe/${recipe.id}`} className={styles.popularTitle}>
                    {recipe.title}
                  </Link>
                  <span className={styles.popularAuthor}>{recipe.authorName}</span>
                </div>
              </div>
            ))}
          </div>
        </section>
      )}

      {/* Pagination */}
      {!searchQuery && !selectedCategory && totalPages > 1 && (
        <div className={styles.pagination}>
          <button
            className={styles.pageButton}
            onClick={() => onPageChange(currentPage - 1)}
            disabled={currentPage === 1}
          >
            <ChevronLeft size={20} />
          </button>
          
          {Array.from({ length: totalPages }, (_, i) => i + 1)
            .filter(page => page === 1 || page === totalPages || Math.abs(page - currentPage) <= 2)
            .reduce<(number | 'ellipsis')[]>((acc, page, idx, arr) => {
              if (idx > 0 && page - (arr[idx - 1] as number) > 1) {
                acc.push('ellipsis');
              }
              acc.push(page);
              return acc;
            }, [])
            .map((item, idx) => 
              item === 'ellipsis' ? (
                <span key={`ellipsis-${idx}`} className={styles.ellipsis}>...</span>
              ) : (
                <button
                  key={item}
                  className={`${styles.pageButton} ${currentPage === item ? styles.activePage : ''}`}
                  onClick={() => onPageChange(item as number)}
                >
                  {item}
                </button>
              )
            )
          }
          
          <button
            className={styles.pageButton}
            onClick={() => onPageChange(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            <ChevronRight size={20} />
          </button>
        </div>
      )}
    </div>
  );
}
