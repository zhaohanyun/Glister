"""routing URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from app import albums
from app import photos
from app import albums

urlpatterns = [
    path('admin/', admin.site.urls),
    path('getalbums/', albums.getAlbums, name='getalbums'),
    path('getfolders/', albums.getFolders, name='getfolders'),
    path('getphotos/', photos.getPhotos, name='getphotos'),
    path('deletephoto/', photos.deletePhoto, name='deletephoto'),
    path('starphoto/', photos.starPhoto, name='starphoto'),
    path('postalbum/', albums.postAlbum, name='postalbum'),
    path('editalbum/', albums.editAlbum, name='editalbum'),
    path('deletealbum/', albums.deleteAlbum, name='deletealbum'),
    path('getfavorites/', photos.getFavorites, name='getfavorites'),
]
