import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, ChefHat, Clock, Users, Star, Heart, MessageCircle, Share2 } from 'lucide-react';
import { recipeApi, favoriteApi, commentApi, ratingApi, getCurrentUser, Recipe, Ingredient, Step, Comment } from '../../services/api';
import './RecipePage.css';

interface RecipePageProps {
  recipes: Recipe[];
  onFavorite: (id: number) => void;
}

export function RecipePage({ recipes, onFavorite }: RecipePageProps) {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [recipe, setRecipe] = useState<Recipe | null>(null);
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [steps, setSteps] = useState<Step[]>([]);
  const [comments, setComments] = useState<Comment[]>([]);
  const [isFavorited, setIsFavorited] = useState(false);
  const [rating, setRating] = useState<{ averageScore: number; totalCount: number }>({ averageScore: 0, totalCount: 0 });
  const [userRating, setUserRating] = useState<number | null>(null);
  const [newComment, setNewComment] = useState('');
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'ingredients' | 'steps'>('ingredients');

  const currentUser = getCurrentUser();

  useEffect(() => {
    loadRecipeDetail();
  }, [id]);

  const loadRecipeDetail = async () => {
    if (!id) return;
    
    setLoading(true);
    try {
      // 从列表中获取食谱基本信息
      const recipeData = recipes.find(r => r.id === Number(id));
      if (recipeData) {
        setRecipe(recipeData);
        
        // 获取详情（食材和步骤）
        try {
          const detail = await recipeApi.getById(Number(id));
          setIngredients(detail.ingredients || []);
          setSteps(detail.steps || []);
        } catch (e) {
          // 如果获取详情失败，使用空数组
          setIngredients([]);
          setSteps([]);
        }
        
        // 获取评论
        try {
          const commentList = await commentApi.getByRecipe(Number(id));
          setComments(commentList);
        } catch (e) {
          setComments([]);
        }
        
        // 获取评分
        try {
          const ratingStats = await ratingApi.getStats(Number(id));
          setRating(ratingStats);
        } catch (e) {
          setRating({ averageScore: 0, totalCount: 0 });
        }
        
        // 检查是否收藏
        if (currentUser) {
          try {
            const fav = await favoriteApi.check(currentUser.id, Number(id));
            setIsFavorited(fav);
          } catch (e) {
            setIsFavorited(false);
          }
          
          // 获取用户评分
          try {
            const ur = await ratingApi.getUserRating(Number(id), currentUser.id);
            setUserRating(ur);
          } catch (e) {
            setUserRating(null);
          }
        }
      }
    } catch (error) {
      console.error('Failed to load recipe:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFavorite = async () => {
    if (!currentUser || !recipe) {
      alert('请先登录');
      return;
    }
    
    try {
      if (isFavorited) {
        await favoriteApi.remove(currentUser.id, recipe.id);
      } else {
        await favoriteApi.add(currentUser.id, recipe.id);
      }
      setIsFavorited(!isFavorited);
      onFavorite(recipe.id);
    } catch (error) {
      console.error('Failed to toggle favorite:', error);
    }
  };

  const handleRating = async (score: number) => {
    if (!currentUser || !recipe) {
      alert('请先登录');
      return;
    }
    
    try {
      const result = await ratingApi.rate(recipe.id, currentUser.id, score);
      setRating(result);
      setUserRating(score);
    } catch (error) {
      console.error('Failed to rate:', error);
    }
  };

  const handleComment = async () => {
    if (!currentUser || !recipe || !newComment.trim()) return;
    
    try {
      const comment = await commentApi.add({
        recipeId: recipe.id,
        userId: currentUser.id,
        userNickname: currentUser.nickname,
        userAvatar: currentUser.avatar,
        content: newComment.trim(),
      });
      setComments([comment, ...comments]);
      setNewComment('');
    } catch (error) {
      console.error('Failed to add comment:', error);
    }
  };

  const handleShare = () => {
    const url = window.location.href;
    if (navigator.share) {
      navigator.share({
        title: recipe?.title,
        text: recipe?.description,
        url: url,
      });
    } else {
      navigator.clipboard.writeText(url);
      alert('链接已复制到剪贴板');
    }
  };

  if (loading) {
    return (
      <div className="recipe-page-loading">
        <div className="loading-spinner"></div>
        <p>加载中...</p>
      </div>
    );
  }

  if (!recipe) {
    return (
      <div className="recipe-page-error">
        <ChefHat size={64} />
        <h2>未找到该食谱</h2>
        <button onClick={() => navigate('/')}>返回首页</button>
      </div>
    );
  }

  return (
    <div className="recipe-page">
      {/* 封面图 */}
      <div className="recipe-hero" style={{ backgroundImage: `url(${recipe.imageUrl})` }}>
        <div className="recipe-hero-overlay">
          <button className="back-button" onClick={() => navigate(-1)}>
            <ArrowLeft size={20} />
            返回
          </button>
          
          <div className="recipe-actions">
            <button className="action-button" onClick={handleShare} title="分享">
              <Share2 size={20} />
            </button>
            <button 
              className={`action-button ${isFavorited ? 'active' : ''}`} 
              onClick={handleFavorite}
              title="收藏"
            >
              <Heart size={20} fill={isFavorited ? 'currentColor' : 'none'} />
            </button>
          </div>
        </div>
      </div>

      {/* 内容区 */}
      <div className="recipe-content">
        {/* 基本信息 */}
        <div className="recipe-header">
          <h1>{recipe.title}</h1>
          <p className="recipe-description">{recipe.description}</p>
          
          <div className="recipe-meta">
            <span className="meta-item">
              <Clock size={16} />
              {recipe.cookTime} 分钟
            </span>
            <span className="meta-item">
              <Users size={16} />
              {recipe.servings} 人份
            </span>
            <span className="meta-item difficulty">{recipe.difficulty}</span>
            <span className="meta-item category">{recipe.category}</span>
          </div>

          {/* 评分 */}
          <div className="recipe-rating">
            <div className="rating-stats">
              <Star size={20} fill="#fbbf24" stroke="#fbbf24" />
              <span className="rating-value">{rating.averageScore.toFixed(1)}</span>
              <span className="rating-count">({rating.totalCount} 人评分)</span>
            </div>
            {currentUser && (
              <div className="rating-stars">
                {[1, 2, 3, 4, 5].map((score) => (
                  <button
                    key={score}
                    className={`star-button ${userRating && userRating >= score ? 'active' : ''}`}
                    onClick={() => handleRating(score)}
                  >
                    <Star size={24} fill={userRating && userRating >= score ? '#fbbf24' : 'none'} stroke="#fbbf24" />
                  </button>
                ))}
              </div>
            )}
          </div>

          {/* 作者信息 */}
          <div className="recipe-author">
            <img src={recipe.authorAvatar} alt={recipe.authorName} className="author-avatar" />
            <div className="author-info">
              <span className="author-label">作者</span>
              <span className="author-name">{recipe.authorName}</span>
            </div>
          </div>
        </div>

        {/* 食材/步骤切换 */}
        <div className="recipe-tabs">
          <button 
            className={`tab-button ${activeTab === 'ingredients' ? 'active' : ''}`}
            onClick={() => setActiveTab('ingredients')}
          >
            食材清单 ({ingredients.length})
          </button>
          <button 
            className={`tab-button ${activeTab === 'steps' ? 'active' : ''}`}
            onClick={() => setActiveTab('steps')}
          >
            烹饪步骤 ({steps.length})
          </button>
        </div>

        {/* 食材列表 */}
        {activeTab === 'ingredients' && (
          <div className="ingredients-section">
            {ingredients.length > 0 ? (
              <ul className="ingredients-list">
                {ingredients.map((ing, index) => (
                  <li key={index} className="ingredient-item">
                    <span className="ingredient-name">{ing.name}</span>
                    <span className="ingredient-amount">{ing.amount}</span>
                  </li>
                ))}
              </ul>
            ) : (
              <p className="empty-message">暂无食材信息</p>
            )}
          </div>
        )}

        {/* 步骤列表 */}
        {activeTab === 'steps' && (
          <div className="steps-section">
            {steps.length > 0 ? (
              <ol className="steps-list">
                {steps.map((step, index) => (
                  <li key={index} className="step-item">
                    <div className="step-number">{step.stepNumber}</div>
                    <div className="step-content">
                      <p>{step.content}</p>
                      {step.imageUrl && (
                        <img src={step.imageUrl} alt={`步骤 ${step.stepNumber}`} className="step-image" />
                      )}
                    </div>
                  </li>
                ))}
              </ol>
            ) : (
              <p className="empty-message">暂无步骤信息</p>
            )}
          </div>
        )}

        {/* 评论区域 */}
        <div className="comments-section">
          <h3>
            <MessageCircle size={20} />
            评论 ({comments.length})
          </h3>
          
          {currentUser && (
            <div className="comment-form">
              <img src={currentUser.avatar} alt="" className="comment-avatar" />
              <div className="comment-input-wrapper">
                <textarea
                  value={newComment}
                  onChange={(e) => setNewComment(e.target.value)}
                  placeholder="发表你的看法..."
                  rows={2}
                />
                <button onClick={handleComment} disabled={!newComment.trim()}>
                  发布
                </button>
              </div>
            </div>
          )}
          
          <div className="comments-list">
            {comments.length > 0 ? (
              comments.map((comment) => (
                <div key={comment.id} className="comment-item">
                  <img src={comment.userAvatar} alt="" className="comment-avatar" />
                  <div className="comment-body">
                    <div className="comment-header">
                      <span className="comment-author">{comment.userNickname}</span>
                      <span className="comment-time">
                        {new Date(comment.createTime).toLocaleDateString()}
                      </span>
                    </div>
                    <p className="comment-content">{comment.content}</p>
                  </div>
                </div>
              ))
            ) : (
              <p className="empty-message">还没有评论，来说两句吧</p>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
