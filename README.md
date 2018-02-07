# Navrick
A simple fragment navigation library



### Warning

Navrick was created to play around with the idea of "Single Activity Apps", and how this could be achieved using reflection and state caching to manage and maintain fragment state outside of the traditional Android lifecycle. Navrick relies on runtime reflection (which is slow) and caches objects in a way that makes it easy to leak memory. 